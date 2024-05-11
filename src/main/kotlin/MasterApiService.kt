import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.call.receive
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import java.io.File
import java.util.UUID

data class FileCreateOperationCreationRequest(
    val fileSize: Long,
    val fileName: String,
    val directoryId: UUID,
)

data class ChunkCreationRequest(
    val order: Long,
    val chunkServerIp: String,
    val chunkServerPort: String,
)

suspend fun createFileCreationRequest(filePath: String): List<ChunkCreationRequest> {
    val file = File(filePath)
    val fileSize = file.length()
    val fileName = file.name
    return send(
        fileSize = fileSize,
        fileName = fileName,
    )
}

private suspend fun send(
    fileSize: Long,
    fileName: String,
): List<ChunkCreationRequest> {
    val masterServerAddress = "http://192.168.199.72:8080"
    println("masterServerAddress: $masterServerAddress")
    val client = HttpClient(CIO)
    val response: HttpResponse = client.post("$masterServerAddress/fileCreate")
    {
        setBody(
            FileCreateOperationCreationRequest(
                fileSize = fileSize,
                fileName = fileName,
                directoryId = UUID.randomUUID(),
            )
        )
    }
    client.close()
    return response.body<List<ChunkCreationRequest>>().also {
        println("res: $it")
    }
}