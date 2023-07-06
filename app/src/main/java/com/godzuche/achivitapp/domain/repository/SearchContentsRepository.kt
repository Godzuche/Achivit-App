package com.godzuche.achivitapp.domain.repository

import com.godzuche.achivitapp.domain.model.SearchResult
import kotlinx.coroutines.flow.Flow

interface SearchContentsRepository {
    fun searchContents(searchQuery: String): Flow<SearchResult>
    fun getSearchContentsCount(): Flow<Int>
}