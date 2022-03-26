package com.angorasix.projects.presentation.application

import com.angorasix.projects.presentation.domain.projectpresentation.ProjectPresentation
import com.angorasix.contributors.domain.contributor.ProjectPresentationRepository
import io.smallrye.mutiny.Multi
import io.smallrye.mutiny.Uni
import org.bson.types.ObjectId
import javax.enterprise.context.ApplicationScoped
import javax.validation.Valid

/**
 *
 *
 * @author rozagerardo
 */
@ApplicationScoped
class ProjectsPresentationService(private val repository: ProjectPresentationRepository) {

    fun findSingleProjectPresentation(id: String): Uni<ProjectPresentation> {
        return if (ObjectId.isValid(id)) {
            repository.findById(ObjectId(id))
        } else {
            Uni.createFrom().nullItem()
        }
    }

    fun findProjectPresentations(): Multi<ProjectPresentation> {
        return repository.streamAll()
    }

    fun createProjectPresentations(@Valid projectPresentation: ProjectPresentation): Uni<ProjectPresentation> {
        return repository.persist(projectPresentation)
    }
}
