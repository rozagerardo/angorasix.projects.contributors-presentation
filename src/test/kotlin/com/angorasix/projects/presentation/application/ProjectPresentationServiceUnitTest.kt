package com.angorasix.projects.presentation.application

import com.angorasix.commons.domain.SimpleContributor
import com.angorasix.projects.presentation.domain.projectpresentation.PresentationSection
import com.angorasix.projects.presentation.domain.projectpresentation.ProjectPresentation
import com.angorasix.projects.presentation.domain.projectpresentation.ProjectPresentationRepository
import com.angorasix.projects.presentation.infrastructure.queryfilters.ListProjectPresentationsFilter
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifyAll
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.mockk
import io.mockk.verifyAll
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.AssertionsForClassTypes.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class ProjectPresentationServiceUnitTest {
    private lateinit var service: ProjectsPresentationService

    @MockK
    private lateinit var repository: ProjectPresentationRepository

    @BeforeEach
    fun init() {
        service = ProjectsPresentationService(repository)
    }

    @Test
    @Throws(Exception::class)
    @kotlinx.coroutines.ExperimentalCoroutinesApi
    fun `given existing projects - when request find projects - then receive projects`() =
        runTest {
            val mockedProjectPresentation = ProjectPresentation(
                "mockedProjectId",
                setOf(SimpleContributor("1", emptySet())),
                "mockedReferenceName",
                emptyList(),
            )
            val filter = ListProjectPresentationsFilter()
            coEvery { repository.findUsingFilter(filter) } returns flowOf(mockedProjectPresentation)

            val outputProjectPresentations = service.findProjectPresentations(filter)

            outputProjectPresentations.collect {
                assertThat<ProjectPresentation>(it).isSameAs(mockedProjectPresentation)
            }
            coVerify { repository.findUsingFilter(filter) }
        }

    @Test
    @Throws(Exception::class)
    @kotlinx.coroutines.ExperimentalCoroutinesApi
    fun givenExistingProjectPresentation_whenFindSingleProjectPresentations_thenServiceRetrievesMonoWithProjectPresentation() =
        runTest {
            val mockedProjectPresentationId = "id1"
            val mockedProjectPresentation = ProjectPresentation(
                "mockedProjectId",
                setOf(SimpleContributor("1", emptySet())),
                "mockedReferenceName",
                emptyList(),
            )
            coEvery { repository.findById(mockedProjectPresentationId) } returns mockedProjectPresentation
            val outputProjectPresentation =
                service.findSingleProjectPresentation(mockedProjectPresentationId)
            assertThat(outputProjectPresentation).isSameAs(mockedProjectPresentation)
            coVerify { repository.findById(mockedProjectPresentationId) }
        }

    @Test
    @Throws(Exception::class)
    @kotlinx.coroutines.ExperimentalCoroutinesApi
    fun whenCreateProjectPresentation_thenServiceRetrieveSavedProjectPresentation() =
        runTest {
            val mockedProjectPresentation = ProjectPresentation(
                "mockedProjectId",
                setOf(SimpleContributor("1", emptySet())),
                "mockedReferenceName",
                emptyList(),
            )
            val savedProjectPresentation = ProjectPresentation(
                "savedMockedProjectId",
                setOf(SimpleContributor("1", emptySet())),
                "mockedReferenceName",
                emptyList(),
            )
            coEvery { repository.save(mockedProjectPresentation) } returns savedProjectPresentation
            val outputProjectPresentation =
                service.createProjectPresentation(mockedProjectPresentation)
            assertThat(outputProjectPresentation).isSameAs(savedProjectPresentation)
            coVerify { repository.save(mockedProjectPresentation) }
        }

    @Test
    @Throws(Exception::class)
    @kotlinx.coroutines.ExperimentalCoroutinesApi
    fun whenUpdateProjectPresentation_thenServiceRetrieveSavedProjectPresentation() =
        runTest {

            val mockedSimpleContributor = SimpleContributor("1", emptySet())
            val mockedExistingProjectPresentation = mockk<ProjectPresentation>()

            every {
                mockedExistingProjectPresentation.setProperty(ProjectPresentation::referenceName.name) value "mockedUpdatedReferenceName"
            } just Runs
            every {
                mockedExistingProjectPresentation.setProperty(ProjectPresentation::sections.name) value emptyList<PresentationSection>()
            } just Runs

            val mockedUpdateProjectPresentation = ProjectPresentation(
                "mockedProjectId",
                setOf(mockedSimpleContributor),
                "mockedUpdatedReferenceName",
                emptyList(),
            )

            val savedProjectPresentation = ProjectPresentation(
                "savedMockedProjectId",
                setOf(mockedSimpleContributor),
                "mockedReferenceName",
                emptyList(),
            )

            coEvery {
                repository.findByIdForContributor(
                        ListProjectPresentationsFilter(
                                listOf("mockedProjectId"),
                                null,
                                listOf("1"),
                                listOf("id1")
                        ),
                        mockedSimpleContributor,
                )
            } returns mockedExistingProjectPresentation

            coEvery { repository.save(any()) } returns savedProjectPresentation

            val outputProjectPresentation =
                service.updateProjectPresentation("id1", mockedUpdateProjectPresentation, mockedSimpleContributor)

            assertThat(outputProjectPresentation).isSameAs(savedProjectPresentation)

            coVerifyAll {
                repository.findByIdForContributor(
                        ListProjectPresentationsFilter(
                                listOf("mockedProjectId"),
                                null,
                                listOf("1"),
                                listOf("id1")
                        ),
                        mockedSimpleContributor,
                )
                repository.save(any())
            }

            verifyAll {
                mockedExistingProjectPresentation.setProperty(ProjectPresentation::referenceName.name) value "mockedUpdatedReferenceName"
                mockedExistingProjectPresentation.setProperty(ProjectPresentation::sections.name) value emptyList<PresentationSection>()
            }

            confirmVerified(mockedExistingProjectPresentation, repository)
        }

    @Test
    @Throws(Exception::class)
    @kotlinx.coroutines.ExperimentalCoroutinesApi
    fun whenUpdateProjectPresentation_thenServiceRetrieveUpdatedProjectPresentation() =
        runTest {
            val mockedProjectPresentation = ProjectPresentation(
                "mockedId",
                setOf(SimpleContributor("1", emptySet())),
                "mockedProjectId",
                emptyList(),
            )
            val updatedProjectPresentation = ProjectPresentation(
                "mockedId",
                setOf(SimpleContributor("1", emptySet())),
                "updatedMockedProjectId",
                emptyList(),
            )
            coEvery { repository.save(mockedProjectPresentation) } returns updatedProjectPresentation
            val outputProjectPresentation =
                service.createProjectPresentation(mockedProjectPresentation)
            assertThat(outputProjectPresentation).isSameAs(updatedProjectPresentation)
            coVerify { repository.save(mockedProjectPresentation) }
        }
}
