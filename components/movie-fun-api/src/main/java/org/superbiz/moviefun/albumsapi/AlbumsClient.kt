package org.superbiz.moviefun.albumsapi

import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpMethod.GET
import org.springframework.web.client.RestOperations
import org.superbiz.moviefun.restsupport.RestTemplate

class AlbumsClient(
        private val albumsUrl: String,
        private val restOperations: RestOperations,
        private val restTemplate: RestTemplate
) {

    fun addAlbum(album: AlbumInfo) {
        restTemplate.post(albumsUrl, album, AlbumInfo::class)
    }

    fun find(id: Long): AlbumInfo {
        val restUrl = albumsUrl + "/" + id
        val result = restTemplate.get(restUrl, AlbumInfo::class)

        when (result) {
            is RestTemplate.RestResult.Error ->
                throw RuntimeException("Error while fetching.... ${result.error}")
            is RestTemplate.RestResult.Success ->
                return result.value
        }
    }

    val albums: List<AlbumInfo>
        get() {
            val albumListType = object : ParameterizedTypeReference<List<AlbumInfo>>() {

            }

            return restOperations.exchange(albumsUrl, GET, null, albumListType).body
        }
}