package es.ruymi.services.users

import es.ruymi.exceptions.UserBadRequestException
import es.ruymi.exceptions.UserNotFoundException
import es.ruymi.exceptions.UserUnauthorizedException
import es.ruymi.models.User
import es.ruymi.repositories.usuario.UsuarioRepository
import kotlinx.coroutines.flow.Flow
import mu.KotlinLogging
import org.koin.core.annotation.Single
import java.util.*

private val logger = KotlinLogging.logger {}

@Single
class UsersServiceImpl(
    private val repository: UsuarioRepository
) : UsersService {

    init {
        logger.debug { "Inicializando el servicio de Usuarios" }
    }

    override suspend fun findAll(): Flow<User> {
        logger.debug { "findAll: Buscando todos los usuarios" }

        return repository.findAll()
    }

    override suspend fun findById(id: UUID): User {
        logger.debug { "findById: Buscando usuario con id: $id" }

        return repository.findById(id) ?: throw UserNotFoundException("No se ha encontrado el usuario con id: $id")
    }

    override suspend fun findByUsername(username: String): User {
        logger.debug { "findByUsername: Buscando usuario con username: $username" }

        return repository.findByUsername(username)
            ?: throw UserNotFoundException("No se ha encontrado el usuario con username: $username")
    }

    override fun hashedPassword(password: String): String {
        logger.debug { "hashedPassword: Hasheando la contraseña" }

        return repository.hashedPassword(password)
    }

    override suspend fun checkUserNameAndPassword(username: String, password: String): User {
        logger.debug { "checkUserNameAndPassword: Comprobando el usuario y contraseña" }

        return repository.checkUserNameAndPassword(username, password)
            ?: throw UserUnauthorizedException("Nombre de usuario o contraseña incorrectos")
    }

    override suspend fun save(entity: User): User {
        logger.debug { "insert: Creando usuario" }

        val existingUser = repository.findByUsername(entity.usuario)
        if (existingUser != null) {
            throw UserBadRequestException("Ya existe un usuario con username: ${entity.usuario}")
        }

        val user =
            entity.copy(
                id = UUID.randomUUID(),
                password = hashedPassword(entity.password),
            )

        return repository.insert(user)
    }

    override suspend fun update(id: UUID, entity: User): User {
        logger.debug { "update: Actualizando usuario con id: $id" }

        // No lo necesitamos, pero lo dejamos por si acaso
        val existingUser = repository.findByUsername(entity.usuario)
        if (existingUser != null && existingUser.id != id) {
            throw UserBadRequestException("Ya existe un usuario con username: ${entity.usuario}")
        }

        val user =
            entity.copy()

        return repository.update(user)!!

    }

    override suspend fun delete(id: UUID): User? {
        logger.debug { "delete: Borrando usuario con id: $id" }

        val user = repository.findById(id)
        user?.let {
            repository.delete(it)
        }
        return user
    }
}