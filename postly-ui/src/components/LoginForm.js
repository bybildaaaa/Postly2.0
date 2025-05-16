import React, { useState } from 'react';
import { Button, TextField, Container, Typography, Alert } from '@mui/material';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';

const LoginForm = () => {
    const [username, setUsername] = useState('');
    const [isRegister, setIsRegister] = useState(false);
    const [error, setError] = useState('');
    const navigate = useNavigate();

    const handleAuth = () => {
        setError('');
        if (!username.trim()) {
            return setError('Введите имя пользователя');
        }

        const url = isRegister
            ? `http://localhost:8080/users?username=${username}`
            : `http://localhost:8080/users/login?username=${username}`;

        axios.post(url)
            .then(res => {
                localStorage.setItem('user', username);
                localStorage.setItem('userId', res.data.id); // сохраняем userId из ответа
                navigate('/home');
            })
            .catch(() => {
                setError(isRegister ? 'Ошибка при регистрации' : 'Такого пользователя не существует');
            });
    };

    return (
        <Container maxWidth="sm" sx={{ mt: 10 }}>
            <Typography variant="h4" gutterBottom>
                {isRegister ? 'Регистрация' : 'Вход'}
            </Typography>

            <TextField
                label="Имя пользователя"
                variant="outlined"
                fullWidth
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                sx={{ mb: 2 }}
            />

            <Button variant="contained" onClick={handleAuth}>
                {isRegister ? 'Зарегистрироваться' : 'Войти'}
            </Button>

            <Button
                variant="text"
                onClick={() => {
                    setIsRegister(!isRegister);
                    setError('');
                }}
                sx={{ mt: 1 }}
            >
                {isRegister ? 'Уже есть аккаунт? Войти' : 'Нет аккаунта? Зарегистрироваться'}
            </Button>

            {error && <Alert severity="error" sx={{ mt: 2 }}>{error}</Alert>}
        </Container>
    );
};

export default LoginForm;
