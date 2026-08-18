package git.alektro3000.messenger.repository.pagingSources

import androidx.paging.PagingSource
import androidx.paging.PagingState
import git.alektro3000.messenger.exception.ServerException
import git.alektro3000.messenger.model.user.UserPreview
import git.alektro3000.messenger.network.dto.ApiResult
import git.alektro3000.messenger.network.dto.toUserPreview
import git.alektro3000.messenger.repository.UserRepository
import git.alektro3000.messenger.viewModel.Notifier


class UsersPagingSource(
    private val repository: UserRepository,
    private val notifier: Notifier,
    private val query: String
) : PagingSource<Int, UserPreview>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, UserPreview> {
        return try {
            val page = params.key ?: 0
            val pageSize = params.loadSize

            val resultResponses = repository.getUsers(
                query = query,
                page = page,
                pageSize = pageSize
            )
            when(resultResponses)
            {
                is ApiResult.Error -> {
                    notifier.show(resultResponses.message)
                    LoadResult.Error(ServerException(resultResponses.message))
                }
                is ApiResult.Success -> {
                    repository.cacheUserPreviews(resultResponses.data)
                    val result = resultResponses.data.map {
                        it.toUserPreview()
                    }

                    LoadResult.Page(
                        data = result,
                        prevKey = if (page == 0) null else page - 1,
                        nextKey = if (result.size != pageSize) null else page + 1
                    )
                }
            }


        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, UserPreview>): Int? {
        return state.anchorPosition?.let { anchor ->
            state.closestPageToPosition(anchor)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchor)?.nextKey?.minus(1)
        }
    }

}
