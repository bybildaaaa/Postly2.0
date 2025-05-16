import React, { useState } from 'react';
import {
    Card, CardContent, Typography, Divider, Button,
    TextField, IconButton, Box
} from '@mui/material';
import FavoriteIcon from '@mui/icons-material/Favorite';
import axios from 'axios';

const PostItem = ({ post, refresh }) => {
    const [comment, setComment] = useState('');
    const currentUser = localStorage.getItem('user');
    const userId = localStorage.getItem('userId'); // предполагается, что ты сохраняешь userId при логине

    const handleLike = () => {
        axios.post('http://localhost:8080/posts/${post.id}/like?userId=${userId}')
    .then(refresh)
            .catch(console.error);
    };

    const handleComment = () => {
        if (!comment.trim()) return;
        axios.post('http://localhost:8080/comments/${post.id}?userId=${userId}&text=${encodeURIComponent(comment)}')
    .then(() => {
            setComment('');
            refresh();
        })
            .catch(console.error);
    };

    const handleDelete = () => {
        axios.delete('http://localhost:8080/posts/${post.id}')
    .then(refresh)
            .catch(console.error);
    };

    return (
        <Card variant="outlined" sx={{ mb: 2 }}>
            <CardContent>
                <Typography variant="h6">{post.username}</Typography>
                <Typography sx={{ mb: 1 }}>{post.content}</Typography>

                <Box display="flex" alignItems="center" gap={1} sx={{ mb: 1 }}>
                    <IconButton onClick={handleLike}>
                        <FavoriteIcon color="error" />
                    </IconButton>
                    <Typography variant="body2">
                        {post.likes?.length || 0}
                    </Typography>
                </Box>

                {post.comments?.length > 0 && (
                    <>
                        <Divider sx={{ my: 1 }} />
                        <Typography variant="subtitle2">Комментарии:</Typography>
                        {post.comments.map(comment => (
                            <Typography key={comment.id} variant="body2" sx={{ pl: 2 }}>
                                — {comment.text}
                            </Typography>
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

                {/* Только владелец может удалить пост */}
                {currentUser === post.username && (
                    <Button size="small" color="error" sx={{ mt: 1 }} onClick={handleDelete}>
                        Удалить пост
                    </Button>
                )}
            </CardContent>
        </Card>
    );
};

export default PostItem;