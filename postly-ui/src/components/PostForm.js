import React, { useState } from 'react';
import { TextField, Button, Box } from '@mui/material';
import axios from 'axios';

const PostForm = ({ refresh }) => {
    const [content, setContent] = useState('');
    const username = localStorage.getItem('user');

    const handleSubmit = () => {
        if (!content.trim()) return;

        const userId = localStorage.getItem('userId');
        const params = new URLSearchParams();
        params.append("userId", userId);
        params.append("text", content);

        axios.post('http://localhost:8080/posts', params, {
            headers: {
                "Content-Type": "application/x-www-form-urlencoded"
            }
        })
            .then(() => {
                setContent('');
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
                value={content}
                onChange={(e) => setContent(e.target.value)}
            />
            <Button variant="contained" sx={{ mt: 1 }} onClick={handleSubmit}>
                Опубликовать
            </Button>
        </Box>
    );
};

export default PostForm;