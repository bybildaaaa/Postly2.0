import React from 'react';
import PostList from '../components/PostList';
import PostForm from '../components/PostForm';
import { Container } from '@mui/material';

const Home = () => {
    return (
        <Container maxWidth="md" sx={{ mt: 4 }}>
            <PostForm />
            <PostList />
        </Container>
    );
};

export default Home;