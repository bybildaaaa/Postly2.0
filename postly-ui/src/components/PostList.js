import React, { useEffect, useState } from 'react';
import axios from 'axios';
import PostItem from './PostItem';

const PostList = () => {
    const [posts, setPosts] = useState([]);

    const fetchPosts = () => {
        axios.get('http://localhost:8080/posts')
            .then(response => setPosts(response.data));
    };

    useEffect(fetchPosts, []);

    return (
        <>
            {posts.map(post => (
                <PostItem key={post.id} post={post} refresh={fetchPosts} />
            ))}
        </>
    );
};

export default PostList;