import { $get, $post, $delete } from './request'

export const postApi = {
  create: (data) => $post('/post', data),
  delete: (id) => $delete('/post/' + id),
  getFeed: (page, pageSize) => $get('/post/feed', { page, pageSize }),
  getUserPosts: (userId, page, pageSize) => $get('/post/user/' + userId, { page, pageSize }),
  getDetail: (id) => $get('/post/' + id),
  like: (id) => $post('/post/' + id + '/like'),
  unlike: (id) => $delete('/post/' + id + '/like'),
  favorite: (id) => $post('/post/' + id + '/favorite'),
  unfavorite: (id) => $delete('/post/' + id + '/favorite'),
  getComments: (postId, page, pageSize) => $get('/comment/' + postId, { page, pageSize }),
  addComment: (postId, content, parentId, replyTo) => $post('/comment/' + postId, { content, parentId, replyTo }),
  follow: (userId) => $post('/follow/' + userId),
  unfollow: (userId) => $delete('/follow/' + userId),
}
