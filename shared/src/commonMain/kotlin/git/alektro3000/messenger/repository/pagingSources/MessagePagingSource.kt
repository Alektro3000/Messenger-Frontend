package git.alektro3000.messenger.repository.pagingSources

import androidx.paging.PagingSource
import androidx.paging.PagingState
import git.alektro3000.messenger.model.chats.MessagePreviewResponse
import git.alektro3000.messenger.network.MessageApi
import git.alektro3000.messenger.network.dto.ApiResult


class MessageRemotePagingSource(
    private val api: MessageApi,
    private val chatId: Long,
    private val beforeMessageId: Long?
) : PagingSource<Long, MessagePreviewResponse>() {

    override suspend fun load(params: LoadParams<Long>): LoadResult<Long, MessagePreviewResponse> {
        return try {
            val beforeMessageId = params.key ?: beforeMessageId
            val pageSize = params.loadSize

            val result = (api.getMessages(
                chatId = chatId,
                beforeMessageId = beforeMessageId,
                pageSize = pageSize
            ) as ApiResult.Success).data

            val nextKey = when {
                result.isEmpty() -> null
                result.size < params.loadSize -> null
                else -> result.last().message.id
            }

            LoadResult.Page(
                data = result,
                prevKey = null,
                nextKey = nextKey
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Long, MessagePreviewResponse>): Long? {
        return state.anchorPosition?.let { pos ->
            state.closestItemToPosition(pos)?.message?.id
        }
    }
}