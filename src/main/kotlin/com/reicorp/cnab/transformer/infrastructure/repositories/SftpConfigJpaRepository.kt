package com.reicorp.cnab.transformer.infrastructure.repositories

import com.reicorp.cnab.transformer.infrastructure.repositories.models.FileRegisterModel
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface FileRegisterRepository : JpaRepository<FileRegisterModel, Long>
