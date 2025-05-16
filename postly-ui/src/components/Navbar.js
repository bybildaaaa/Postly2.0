import React from 'react';
import { AppBar, Toolbar, Typography, Button } from '@mui/material';
import { useNavigate } from 'react-router-dom';

const Navbar = () => {
    const navigate = useNavigate();
    const username = localStorage.getItem('user');

    return (
        <AppBar position="static">
            <Toolbar>
                <Typography variant="h6" sx={{ flexGrow: 1, cursor: 'pointer' }} onClick={() => navigate('/home')}>
                    Postly
                </Typography>

                {username && (
                    <Button color="inherit" onClick={() => navigate('/profile')}>
                        Профиль
                    </Button>
                )}
            </Toolbar>
        </AppBar>
    );
};

export default Navbar;