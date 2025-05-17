import React, { useState, useEffect } from 'react';
import {
    Card, CardContent, Typography, Divider, Button,
    TextField, IconButton, Box
} from '@mui/material';
import FavoriteIcon from '@mui/icons-material/Favorite';
import DeleteIcon from '@mui/icons-material/Delete';
import axios from 'axios';

const PostItem = ({ post, refresh }) => {
    const [comment, setComment] = useState('');
    const [liked, setLiked] = useState(false);
    const [totalLikes, setTotalLikes] = useState(post.likesCount || 0);
    const [comments, setComments] = useState([]);
    const [showComments, setShowComments] = useState(false);
    const userId = localStorage.getItem('userId');
    const currentUser = localStorage.getItem('user');

    // Загрузка статуса лайка
    useEffect(() => {
        axios.get(`http://localhost:8080/posts/${post.id}/liked?userId=${userId}`)
            .then(res => setLiked(res.data))
            .catch(err => console.error('Ошибка проверки лайка:', err));
    }, [post.id, userId]);

    // Загрузка комментариев
    useEffect(() => {
        axios.get(`http://localhost:8080/comments?postId=${post.id}`)
            .then(res => setComments(res.data))
            .catch(err => console.error('Ошибка загрузки комментариев:', err));
    }, [post.id]);

    const toggleLike = () => {
        const method = liked ? 'delete' : 'post';
        axios({
            method: method,
            url: `http://localhost:8080/posts/${post.id}/like?userId=${userId}`
        })
            .then(() => {
                setLiked(!liked);
                setTotalLikes(liked ? totalLikes - 1 : totalLikes + 1);
                refresh();
            })
            .catch(err => console.error('Ошибка при лайке:', err));
    };

    const handleComment = () => {
        if (!comment.trim()) return;
        axios.post(`http://localhost:8080/comments/${post.id}?userId=${userId}&text=${encodeURIComponent(comment)}`)
            .then(() => {
                setComment('');
                axios.get(`http://localhost:8080/comments?postId=${post.id}`)
                    .then(res => setComments(res.data))
                    .catch(err => console.error('Ошибка загрузки комментариев:', err));
                refresh();
            })
            .catch(err => console.error('Ошибка при добавлении комментария:', err));
    };

    const toggleComments = () => {
        setShowComments(!showComments);
    };

    const handleDeletePost = () => {
        axios.delete(`http://localhost:8080/posts/${post.id}`)
            .then(() => refresh())
            .catch(err => console.error('Ошибка при удалении поста:', err));
    };

    const handleDeleteComment = (commentId) => {
        axios.delete(`http://localhost:8080/comments/${commentId}`)
            .then(() => {
                setComments(comments.filter(comment => comment.id !== commentId));
                refresh();
            })
            .catch(err => console.error('Ошибка при удалении комментария:', err));
    };

    return (
        <Card variant="outlined" sx={{ mb: 2 }}>
            <CardContent>
                <Box display="flex" justifyContent="space-between" alignItems="center">
                    <Typography variant="h6">{post.username}</Typography>
                    {post.username === currentUser && (
                        <IconButton color="error" onClick={handleDeletePost}>
                            <DeleteIcon />
                        </IconButton>
                    )}
                </Box>
                <Typography sx={{ mb: 1 }}>{post.post}</Typography>

                <Box display="flex" alignItems="center" gap={1} sx={{ mb: 1 }}>
                    <IconButton onClick={toggleLike}>
                        <FavoriteIcon color={liked ? "error" : "action"} />
                    </IconButton>
                    <Typography variant="body2">{totalLikes}</Typography>
                </Box>

                {comments.length > 0 && (
                    <Button size="small" onClick={toggleComments} sx={{ mb: 1 }}>
                        {showComments ? 'Скрыть комментарии' : `Показать комментарии (${comments.length})`}
                    </Button>
                )}

                {showComments && comments.length > 0 && (
                    <>
                        <Divider sx={{ my: 1 }} />
                        <Typography variant="subtitle2">Комментарии:</Typography>
                        {comments.map(comment => (
                            <Box key={comment.id} display="flex" justifyContent="space-between" sx={{ pl: 2 }}>
                                <Typography variant="body2">
                                    — {comment.username}: {comment.text}
                                </Typography>
                                {comment.username === currentUser && (
                                    <IconButton
                                        size="small"
                                        color="error"
                                        onClick={() => handleDeleteComment(comment.id)}
                                    >
                                        <DeleteIcon fontSize="small" />
                                    </IconButton>
                                )}
                            </Box>
                        ))}
                    </>
                )}

                <Box sx={{ mt: 2 }}>
                    <TextField
                        placeholder="Добавить комментарий..."
                        size="small"
                        fullWidth
                        value={comment}
                        onChange={(e) => setComment(e.target.value)}
                        sx={{ mb: 1 }}
                    />
                    <Button size="small" onClick={handleComment} variant="outlined">Комментировать</Button>
                </Box>
            </CardContent>
        </Card>
    );
};

export default PostItem;