import React, { useEffect, useState } from 'react';
import {
    Container,
    Typography,
    Button,
    Card,
    CardContent,
    Divider,
    IconButton,
    Box
} from '@mui/material';
import FavoriteIcon from '@mui/icons-material/Favorite';
import { useNavigate } from 'react-router-dom'; // ✅ добавлен импорт
import axios from 'axios';

const Profile = () => {
    const [posts, setPosts] = useState([]);
    const username = localStorage.getItem('user');
    const userId = localStorage.getItem('userId'); // получаем userId
    const navigate = useNavigate();

    useEffect(() => {
        axios
            .get('http://localhost:8080/posts')
            .then((res) => {
                const userPosts = res.data.filter((post) => post.username === username);
                setPosts(userPosts);
            })
            .catch((err) => console.error(err));
    }, [username]);

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

    return (
        <Container maxWidth="md" sx={{ mt: 4 }}>
            <Typography variant="h4" gutterBottom>
                Профиль: {username}
            </Typography>

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
                        <Typography variant="body1">{post.content}</Typography>

                        {post.comments && post.comments.length > 0 && (
                            <>
                                <Divider sx={{ my: 1 }} />
                                <Typography variant="subtitle2">Комментарии:</Typography>
                                {post.comments.map((comment) => (
                                    <Typography key={comment.id} variant="body2" sx={{ ml: 2 }}>
                                        - {comment.text}
                                    </Typography>
                                ))}
                            </>
                        )}

                        <Divider sx={{ my: 1 }} />
                        <Box display="flex" alignItems="center">
                            <IconButton disabled>
                                <FavoriteIcon color="error" />
                            </IconButton>
                            <Typography variant="body2">{post.likes ?? 0}</Typography>
                        </Box>
                    </CardContent>
                </Card>
            ))}
        </Container>
    );
};

export default Profile;
