package com.godzuche.achivitapp.core.domain.usecase

import com.godzuche.achivitapp.core.domain.model.SearchResult
import com.godzuche.achivitapp.core.domain.repository.SearchContentsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * A use case which returns the searched contents matched with the search query.
 */
class GetSearchContentsUseCase @Inject constructor(
    private val searchContentsRepository: SearchContentsRepository
) {
    operator fun invoke(
        searchQuery: String
    ): Flow<SearchResult> =
        searchContentsRepository.searchContents(searchQuery)
}