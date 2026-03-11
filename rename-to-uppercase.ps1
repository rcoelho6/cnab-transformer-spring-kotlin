<#
rename-to-uppercase.ps1

Renomeia arquivos (e opcionalmente diretórios) recursivamente convertendo o NOME BASE para MAIÚSCULAS e truncando o nome base para no máximo 25 caracteres (sem alterar a extensão).
Uso seguro: o script renomeia cada item em duas etapas (nome temporário -> nome final) para contornar o comportamento case-insensitive do Windows.

Parametros:
  -RootPath <string>   : pasta raiz onde aplicar (default = current directory)
  -WhatIf              : se passado, só simula (dry-run) e imprime as alterações
  -RenameDirs          : se passado, renomeia também diretórios (cuidado com recursão)

Exemplos:
  # Dry-run na pasta atual
  .\rename-to-uppercase.ps1 -WhatIf

  # Aplicar na pasta específica
  .\rename-to-uppercase.ps1 -RootPath 'C:\Users\rcoel\Componentes\transformer-spring-kotlin'

  # Aplicar e renomear também diretórios
  .\rename-to-uppercase.ps1 -RootPath 'C:\meu\projeto' -RenameDirs

Observações:
- Mantém a extensão original. A transformação é aplicada ao nome base (sem extensão).
- Se ocorrer colisão (dois nomes truncados iguais), o script adiciona um sufixo numérico (-1, -2, ...) garantindo unicidade e ajustando o truncamento para caber o sufixo.
- Recomendo executar primeiro com -WhatIf e depois com git commit apropriado.
#>

param(
    [string]$RootPath = ".",
    [switch]$WhatIf,
    [switch]$RenameDirs
)

function Get-TruncatedBase {
    param(
        [string]$base,
        [int]$maxLen
    )
    if (-not $base) { return $base }
    $b = $base.ToUpper()
    if ($b.Length -le $maxLen) { return $b }
    return $b.Substring(0, $maxLen)
}

function Get-UniqueFinalName {
    param(
        [string]$dir,
        [string]$origBase,
        [string]$ext,
        [int]$maxBaseLen
    )
    $base = $origBase.ToUpper()
    $candidateBase = Get-TruncatedBase -base $base -maxLen $maxBaseLen
    $candidate = "$candidateBase$ext"
    $i = 1
    while (Test-Path (Join-Path $dir $candidate)) {
        $suffix = "-$i"
        $allowed = $maxBaseLen - $suffix.Length
        if ($allowed -lt 1) { $allowed = 1 }
        $candidateBase = if ($base.Length -le $allowed) { $base } else { $base.Substring(0, $allowed) }
        $candidate = "$candidateBase$suffix$ext"
        $i++
        # safety bail-out
        if ($i -gt 1000) { throw "Unable to find unique name in $dir for $origBase$ext" }
    }
    return $candidate
}

# Normalize root path
$root = Resolve-Path -LiteralPath $RootPath -ErrorAction Stop | Select-Object -ExpandProperty Path
Write-Output "Root path: $root"

$maxBaseLen = 25
$renamedCount = 0
$skippedCount = 0
$errorCount = 0

# Process files grouped by directory so we can ensure uniqueness per directory
$files = Get-ChildItem -Path $root -File -Recurse -ErrorAction SilentlyContinue
$groups = $files | Group-Object -Property DirectoryName

foreach ($g in $groups) {
    $dir = $g.Name
    foreach ($file in $g.Group) {
        try {
            $origName = $file.Name
            $base = [System.IO.Path]::GetFileNameWithoutExtension($origName)
            $ext = [System.IO.Path]::GetExtension($origName)
            $finalNameCandidate = Get-TruncatedBase -base $base -maxLen $maxBaseLen
            if (-not $finalNameCandidate) { Write-Warning "Empty base name for $($file.FullName), skipping"; $skippedCount++; continue }

            $finalName = Get-UniqueFinalName -dir $dir -origBase $base -ext $ext -maxBaseLen $maxBaseLen

            if ($finalName -eq $origName) {
                # Already matches target case/length (or case-insensitive equality). But maybe case differs only.
                # We still attempt case-only rename via temp to ensure proper case in filesystem/git.
                if ($origName -ne $finalName) {
                    # case-only difference
                    if ($WhatIf) {
                        Write-Output "Would case-normalize: '$($file.FullName)' -> '$finalName'"
                    } else {
                        $tmp = "$($origName).tmp.$([guid]::NewGuid().ToString().Substring(0,8))"
                        Rename-Item -LiteralPath (Join-Path $dir $origName) -NewName $tmp
                        Rename-Item -LiteralPath (Join-Path $dir $tmp) -NewName $finalName
                        Write-Output "Case-normalized: '$($file.FullName)' -> '$finalName'"
                        $renamedCount++
                    }
                } else {
                    $skippedCount++
                }
            } else {
                if ($WhatIf) {
                    Write-Output "Would rename: '$($file.FullName)' -> '$finalName'"
                } else {
                    # two-step rename to handle case-insensitive FS
                    $tmp = "$($origName).tmp.$([guid]::NewGuid().ToString().Substring(0,8))"
                    Rename-Item -LiteralPath (Join-Path $dir $origName) -NewName $tmp
                    Rename-Item -LiteralPath (Join-Path $dir $tmp) -NewName $finalName
                    Write-Output "Renamed: '$($file.FullName)' -> '$finalName'"
                    $renamedCount++
                }
            }
        } catch {
            Write-Warning "Error renaming file $($file.FullName): $_"
            $errorCount++
        }
    }
}

# Optionally rename directories (bottom-up to avoid moving parent before children)
if ($RenameDirs) {
    $dirs = Get-ChildItem -Path $root -Directory -Recurse -ErrorAction SilentlyContinue | Sort-Object FullName -Descending
    foreach ($d in $dirs) {
        try {
            $origName = $d.Name
            $parent = $d.Parent.FullName
            $finalBase = Get-TruncatedBase -base $origName -maxLen $maxBaseLen
            $finalDirName = Get-UniqueFinalName -dir $parent -origBase $origName -ext "" -maxBaseLen $maxBaseLen
            if ($finalDirName -eq $origName) { continue }
            if ($WhatIf) {
                Write-Output "Would rename dir: '$($d.FullName)' -> '$finalDirName'"
            } else {
                $tmp = "$($origName).tmp.$([guid]::NewGuid().ToString().Substring(0,8))"
                Rename-Item -LiteralPath (Join-Path $parent $origName) -NewName $tmp
                Rename-Item -LiteralPath (Join-Path $parent $tmp) -NewName $finalDirName
                Write-Output "Renamed dir: '$($d.FullName)' -> '$finalDirName'"
            }
        } catch {
            Write-Warning "Error renaming dir $($d.FullName): $_"
            $errorCount++
        }
    }
}

Write-Output "Summary: renamed=$renamedCount, skipped=$skippedCount, errors=$errorCount"

if ($WhatIf) { Write-Output "Dry-run mode: no changes were applied." }
else { Write-Output "Run completed. Review changes and commit with git if desired." }

