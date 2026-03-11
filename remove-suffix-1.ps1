<#
remove-suffix-1.ps1

Remove the trailing "-1" from filenames (before extension) recursively under a root path.
Performs safe two-step rename (tmp -> final) to ensure Windows handles case changes and Git detects renames.
If a collision occurs (final name already exists), it will append -2, -3, ... to make unique.

Usage:
  .\remove-suffix-1.ps1 -RootPath 'C:\path\to\folder' [-WhatIf]

#>
param(
  [string]$RootPath = ".",
  [switch]$WhatIf
)

$root = Resolve-Path -LiteralPath $RootPath -ErrorAction Stop | Select-Object -ExpandProperty Path
Write-Output "Root: $root"

$files = Get-ChildItem -Path $root -File -Recurse -ErrorAction SilentlyContinue | Where-Object { $_.BaseName -match '-1$' }
if (-not $files) { Write-Output 'No files found with suffix -1'; exit 0 }

foreach ($file in $files) {
  try {
    $dir = $file.DirectoryName
    $orig = $file.Name
    $base = [System.IO.Path]::GetFileNameWithoutExtension($orig)
    $ext = [System.IO.Path]::GetExtension($orig)
    $newBase = $base -replace '-1$',''
    if (-not $newBase) { Write-Warning "Skipping $orig because base became empty after removing -1"; continue }

    # compute unique final name
    $candidate = "$newBase$ext"
    $targetPath = Join-Path $dir $candidate
    $i = 2
    while (Test-Path $targetPath) {
      $candidate = "$newBase-$i$ext"
      $targetPath = Join-Path $dir $candidate
      $i++
      if ($i -gt 1000) { throw "Unable to find unique name for $orig" }
    }

    $finalName = [System.IO.Path]::GetFileName($targetPath)

    if ($WhatIf) {
      Write-Output "Would rename: $orig -> $finalName"
    } else {
      $tmp = "$orig.tmp.$([guid]::NewGuid().ToString().Substring(0,8))"
      Rename-Item -LiteralPath (Join-Path $dir $orig) -NewName $tmp
      Rename-Item -LiteralPath (Join-Path $dir $tmp) -NewName $finalName
      Write-Output "Renamed: $orig -> $finalName"
    }
  } catch {
    Write-Warning "Error processing $($file.FullName): $_"
  }
}

Write-Output 'Done.'

