import React, { useEffect, useState } from 'react';
import {
    Container,
    Typography,
    Button,
    Card,
    CardContent,
    Divider,
    IconButton,
    Box,
    TextField
} from '@mui/material';
import FavoriteIcon from '@mui/icons-material/Favorite';
import DeleteIcon from '@mui/icons-material/Delete';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';

const Profile = () => {
    const [posts, setPosts] = useState([]);
    const [username, setUsername] = useState(localStorage.getItem('user') || '');
    const [newUsername, setNewUsername] = useState('');
    const [isEditingUsername, setIsEditingUsername] = useState(false);
    const [usernameError, setUsernameError] = useState('');
    const userId = localStorage.getItem('userId');
    const navigate = useNavigate();

    useEffect(() => {
        axios.get('http://localhost:8080/posts')
            .then((res) => {
                const userPosts = res.data
                    .filter((post) => post.username === username)
                    .map(post => ({ ...post, showComments: false, comments: [], liked: false }));
                setPosts(userPosts);

                // Загружаем комментарии и статус лайков для каждого поста
                userPosts.forEach(post => {
                    // Комментарии
                    axios.get(`http://localhost:8080/comments?postId=${post.id}`)
                        .then(res => {
                            setPosts(prev => prev.map(p =>
                                p.id === post.id ? { ...p, comments: res.data } : p
                            ));
                        })
                        .catch(err => console.error(`Ошибка загрузки комментариев для поста ${post.id}:`, err));

                    // Статус лайка
                    axios.get(`http://localhost:8080/posts/${post.id}/liked?userId=${userId}`)
                        .then(res => {
                            setPosts(prev => prev.map(p =>
                                p.id === post.id ? { ...p, liked: res.data } : p
                            ));
                        })
                        .catch(err => console.error(`Ошибка проверки лайка для поста ${post.id}:`, err));
                });
            })
            .catch((err) => console.error('Ошибка загрузки постов:', err));
    }, [username, userId]);

    const handleLogout = () => {
        localStorage.removeItem('user');
        localStorage.removeItem('userId');
        navigate('/');
    };

    const handleDeleteAccount = () => {
        axios.delete(`http://localhost:8080/users/${userId}`).then(() => {
            handleLogout();
        }).catch(err => {
            console.error('Ошибка при удалении аккаунта:', err);
        });
    };

    const toggleComments = (postId) => {
        setPosts(posts.map(post =>
            post.id === postId ? { ...post, showComments: !post.showComments } : post
        ));
    };

    const toggleLike = (postId) => {
        const post = posts.find(p => p.id === postId);
        const method = post.liked ? 'delete' : 'post';
        axios({
            method: method,
            url: `http://localhost:8080/posts/${postId}/like?userId=${userId}`
        })
            .then(() => {
                setPosts(posts.map(p =>
                    p.id === postId
                        ? { ...p, liked: !p.liked, likesCount: p.liked ? p.likesCount - 1 : p.likesCount + 1 }
                        : p
                ));
            })
            .catch(err => console.error('Ошибка при лайке:', err));
    };

    const handleDeletePost = (postId) => {
        axios.delete(`http://localhost:8080/posts/${postId}`)
            .then(() => {
                setPosts(posts.filter(post => post.id !== postId));
            })
            .catch(err => console.error('Ошибка при удалении поста:', err));
    };

    const handleDeleteComment = (postId, commentId) => {
        axios.delete(`http://localhost:8080/comments/${commentId}`)
            .then(() => {
                setPosts(posts.map(post =>
                    post.id === postId
                        ? { ...post, comments: post.comments.filter(comment => comment.id !== commentId) }
                        : post
                ));
            })
            .catch(err => console.error('Ошибка при удалении комментария:', err));
    };

    const handleEditUsername = () => {
        setIsEditingUsername(true);
        setNewUsername(username);
        setUsernameError('');
    };

    const handleSaveUsername = () => {
        if (!newUsername.trim()) {
            setUsernameError('Имя пользователя не может быть пустым');
            return;
        }

        axios.patch(`http://localhost:8080/users/${userId}/${encodeURIComponent(newUsername)}`)
            .then(() => {
                localStorage.setItem('user', newUsername);
                setUsername(newUsername);
                setIsEditingUsername(false);
                setUsernameError('');
                // Обновляем посты и комментарии с новым именем
                setPosts(posts.map(post => ({
                    ...post,
                    username: newUsername,
                    comments: post.comments.map(comment => ({
                        ...comment,
                        username: newUsername
                    }))
                })));
            })
            .catch(err => {
                console.error('Ошибка при изменении имени:', err);
                setUsernameError(
                    err.response?.data?.includes('already exists')
                        ? 'Это имя уже занято'
                        : 'Не удалось изменить имя пользователя'
                );
            });
    };

    const handleCancelEditUsername = () => {
        setIsEditingUsername(false);
        setNewUsername('');
        setUsernameError('');
    };

    return (
        <Container maxWidth="md" sx={{ mt: 4 }}>
            <Box display="flex" alignItems="center" gap={2} sx={{ mb: 3 }}>
                <Typography variant="h4">
                    Профиль: {username}
                </Typography>
                {!isEditingUsername && (
                    <Button variant="outlined" onClick={handleEditUsername}>
                        Изменить имя
                    </Button>
                )}
            </Box>

            {isEditingUsername && (
                <Box sx={{ mb: 3 }}>
                    <TextField
                        label="Новое имя пользователя"
                        variant="outlined"
                        value={newUsername}
                        onChange={(e) => setNewUsername(e.target.value)}
                        error={!!usernameError}
                        helperText={usernameError}
                        sx={{ mr: 1, width: '300px' }}
                    />
                    <Button variant="contained" onClick={handleSaveUsername} sx={{ mt: 1, mr: 1 }}>
                        Сохранить
                    </Button>
                    <Button
                        variant="text"
                        onClick={handleCancelEditUsername}
                        sx={{ mt: 1 }}
                    >
                        Отмена
                    </Button>
                </Box>
            )}

            <Box sx={{ mb: 3, display: 'flex', gap: 2 }}>
                <Button variant="outlined" color="error" onClick={handleLogout}>
                    Выйти из аккаунта
                </Button>
                <Button variant="contained" color="error" onClick={handleDeleteAccount}>
                    Удалить аккаунт
                </Button>
            </Box>

            {posts.map((post) => (
                <Card key={post.id} variant="outlined" sx={{ mb: 2 }}>
                    <CardContent>
                        <Box display="flex" justifyContent="space-between" alignItems="center">
                            <Typography variant="h6">{post.username}</Typography>
                            {post.username === username && (
                                <IconButton color="error" onClick={() => handleDeletePost(post.id)}>
                                    <DeleteIcon />
                                </IconButton>
                            )}
                        </Box>
                        <Typography variant="body1">{post.post}</Typography>

                        <Box display="flex" alignItems="center" gap={1} sx={{ my: 1 }}>
                            <IconButton onClick={() => toggleLike(post.id)}>
                                <FavoriteIcon color={post.liked ? "error" : "action"} />
                            </IconButton>
                            <Typography variant="body2">{post.likesCount || 0}</Typography>
                        </Box>

                        {post.comments.length > 0 && (
                            <Button size="small" onClick={() => toggleComments(post.id)} sx={{ mb: 1 }}>
                                {post.showComments ? 'Скрыть комментарии' : `Показать комментарии (${post.comments.length})`}
                            </Button>
                        )}

                        {post.showComments && post.comments.length > 0 && (
                            <>
                                <Divider sx={{ my: 1 }} />
                                <Typography variant="subtitle2">Комментарии:</Typography>
                                {post.comments.map((comment) => (
                                    <Box key={comment.id} display="flex" justifyContent="space-between" sx={{ pl: 2 }}>
                                        <Typography variant="body2">
                                            - {comment.username}: {comment.text}
                                        </Typography>
                                        {comment.username === username && (
                                            <IconButton
                                                size="small"
                                                color="error"
                                                onClick={() => handleDeleteComment(post.id, comment.id)}
                                            >
                                                <DeleteIcon fontSize="small" />
                                            </IconButton>
                                        )}
                                    </Box>
                                ))}
                            </>
                        )}
                    </CardContent>
                </Card>
            ))}
        </Container>
    );
};

export default Profile;