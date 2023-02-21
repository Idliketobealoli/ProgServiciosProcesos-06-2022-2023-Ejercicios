package es.ruymi.services.storage

import es.ruymi.config.StorageConfig
import io.ktor.utils.io.*
import java.io.File

interface StorageService {
    fun getConfig(): StorageConfig
    fun initStorageDirectory()
    suspend fun saveFile(fileName: String, fileBytes: ByteArray): Map<String, String>
    suspend fun saveFile(fileName: String, fileBytes: ByteReadChannel): Map<String, String>
    suspend fun getFile(fileName: String): File
    suspend fun deleteFile(fileName: String)
}