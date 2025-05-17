import React, { useState } from 'react';
import { TextField, Button, Box } from '@mui/material';
import axios from 'axios';

const PostForm = ({ refresh }) => {
    const [postText, setPostText] = useState(''); // Переименовано для ясности
    const userId = localStorage.getItem('userId');

    const handleSubmit = () => {
        if (!postText.trim()) return;

        const params = new URLSearchParams();
        params.append("userId", userId);
        params.append("text", postText); // Используем "text" как в бэкенде

        axios.post('http://localhost:8080/posts', params, {
            headers: {
                "Content-Type": "application/x-www-form-urlencoded"
            }
        })
            .then(() => {
                setPostText('');
                refresh?.();
            })
            .catch(console.error);
    };

    return (
        <Box sx={{ mb: 3 }}>
            <TextField
                label="Что у вас нового?"
                multiline
                fullWidth
                value={postText}
                onChange={(e) => setPostText(e.target.value)}
            />
            <Button variant="contained" sx={{ mt: 1 }} onClick={handleSubmit}>
                Опубликовать
            </Button>
        </Box>
    );
};

export default PostForm;