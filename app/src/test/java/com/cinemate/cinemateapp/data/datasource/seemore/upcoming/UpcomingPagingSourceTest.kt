package com.cinemate.cinemateapp.data.datasource.seemore.upcoming

import androidx.paging.PagingSource
import com.cinemate.cinemateapp.data.mapper.toUpcomingMovie
import com.cinemate.cinemateapp.data.model.Movie
import com.cinemate.cinemateapp.data.source.network.model.upcoming.DatesUpcomingResultResponse
import com.cinemate.cinemateapp.data.source.network.model.upcoming.ResultUpcomingMovieResponse
import com.cinemate.cinemateapp.data.source.network.model.upcoming.UpcomingMovieResponse
import com.cinemate.cinemateapp.data.source.network.service.AppService
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class UpcomingPagingSourceTest {
    @MockK
    lateinit var service: AppService
    private lateinit var ds: PagingSource<Int, Movie>

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        ds = UpcomingPagingSource(service)
    }

    @Test
    fun load() {
        runBlocking {
            val page = 1
            val loadParams = mockk<PagingSource.LoadParams<Int>>(relaxed = true)
            val expectedUpcomingMovieResponse =
                UpcomingMovieResponse(
                    dates =
                        DatesUpcomingResultResponse(
                            maximum = "2024-05-17",
                            minimum = "2024-05-01",
                        ),
                    page = 1,
                    results =
                        listOf(
                            ResultUpcomingMovieResponse(
                                adult = false,
                                backdropPath = "/backdrop1.jpg",
                                genreIds = listOf(28, 12, 14),
                                id = 123,
                                originalLanguage = "en",
                                originalTitle = "Original Title 1",
                                overview = "Overview of movie 1",
                                popularity = 123.45,
                                posterPath = "/poster1.jpg",
                                releaseDate = "2024-05-17",
                                title = "Movie 1",
                                video = false,
                                voteAverage = 7.8,
                                voteCount = 1000,
                            ),
                            ResultUpcomingMovieResponse(
                                adult = false,
                                backdropPath = "/backdrop2.jpg",
                                genreIds = listOf(18, 36, 53),
                                id = 456,
                                originalLanguage = "en",
                                originalTitle = "Original Title 2",
                                overview = "Overview of movie 2",
                                popularity = 67.89,
                                posterPath = "/poster2.jpg",
                                releaseDate = "2024-05-18",
                                title = "Movie 2",
                                video = false,
                                voteAverage = 8.5,
                                voteCount = 1500,
                            ),
                        ),
                    totalPages = 5,
                    totalResults = 50,
                )
            coEvery { service.upcoming(page) } returns expectedUpcomingMovieResponse
            val loadResult = ds.load(loadParams)
            if (loadResult is PagingSource.LoadResult.Page) {
                assertEquals(
                    expectedUpcomingMovieResponse.results?.map { it.toUpcomingMovie() } ?: expectedUpcomingMovieResponse,
                    loadResult.data,
                )
                assertEquals(null, loadResult.prevKey)
                assertEquals(
                    if (expectedUpcomingMovieResponse.results.isNullOrEmpty()) null else page + 1,
                    loadResult.nextKey,
                )
            }
        }
    }
}
