package com.dik.torrentlist.main

import com.dik.common.Result
import com.dik.themoviedb.SearchTheMovieDbApi
import com.dik.themoviedb.errors.TheMovieDbError
import com.dik.themoviedb.model.Movie
import com.dik.themoviedb.model.TvShow
import com.dik.torrentlist.screens.main.FindPosterForTorrent
import com.dik.torrentlist.screens.main.FindPosterForTorrentErrors
import com.dik.torrserverapi.model.Torrent
import com.dik.videofilenameparser.parseFileNameBase
import com.dik.videofilenameparser.parseFileNameTvShow
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FindPosterForTorrentTest {

    private val searchTheMovieDbApi: SearchTheMovieDbApi = mockk()
    private val findPosterForTorrent = FindPosterForTorrent(searchTheMovieDbApi)

    @Test
    fun `Returns poster when search is successful for Movie`() = runTest {
        val torrent: Torrent = mockk<Torrent>(relaxed = true) {
            every { name } returns "At the edge of the abyss.1080p.rus.mkv"
        }
        val content: Movie = mockk<Movie>(relaxed = true) {
            every { poster300 } returns "poster_url"
        }

        val title = parseFileNameBase(torrent.name).title
        coEvery { searchTheMovieDbApi.multiSearching(title) } returns Result.Success(listOf(content))

        val result = findPosterForTorrent.invoke(torrent)

        assertTrue(result is Result.Success)
        assertEquals("poster_url", result.data.poster300)
    }

    @Test
    fun `Returns Poster when search is successful for TvShow`() = runTest {
        val torrent: Torrent = mockk<Torrent>(relaxed = true) {
            every { name } returns "At the edge of the abyss.S01E04.1080p.rus.mkv"
        }
        val content: TvShow = mockk<TvShow>(relaxed = true) {
            every { poster300 } returns "poster_url"
        }

        val title = parseFileNameTvShow(torrent.name)!!.title
        coEvery { searchTheMovieDbApi.multiSearching(title) } returns Result.Success(listOf(content))

        val result = findPosterForTorrent.invoke(torrent)

        assertTrue(result is Result.Success)
        assertEquals("poster_url", result.data.poster300)
    }

    @Test
    fun `Returns PosterNotFound when no content is found`() = runTest {
        val torrent: Torrent = mockk<Torrent>(relaxed = true) {
            every { name } returns "Unknown Video.mkv"
        }
        val title = parseFileNameBase(torrent.name).title
        coEvery { searchTheMovieDbApi.multiSearching(title) } returns Result.Success(emptyList())

        val result = findPosterForTorrent.invoke(torrent)

        assertTrue(result is Result.Error)
        assertTrue(result.error is FindPosterForTorrentErrors.PosterNotFound)
    }

    @Test
    fun `Returns UnknownError when search fails`() = runTest {
        val torrent: Torrent = mockk<Torrent>(relaxed = true) {
            every { name } returns "Unknown Video.mkv"
        }
        val title = parseFileNameBase(torrent.name).title

        coEvery { searchTheMovieDbApi.multiSearching(title) } returns
                Result.Error(TheMovieDbError.HttpError.ResponseReturnNull)

        val result = findPosterForTorrent.invoke(torrent)

        assertTrue(result is Result.Error)
        assertEquals((result.error as FindPosterForTorrentErrors.UnknownError).error,
            TheMovieDbError.HttpError.ResponseReturnNull.toString())
    }
}