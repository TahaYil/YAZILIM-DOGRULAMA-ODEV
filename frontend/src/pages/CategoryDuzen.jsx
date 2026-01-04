import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import axios from 'axios';
import {
  Box,
  TextField,
  Button,
  Typography,
  Paper,
  Grid,
} from '@mui/material';

// Configure axios defaults
axios.defaults.baseURL = 'http://localhost:8080';

// Add request interceptor to include JWT token
axios.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

export default function CategoryDuzen() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [title, setTitle] = useState('');

  useEffect(() => {
    const fetchCategory = async () => {
      try {
        const response = await axios.get(`/category/${id}`);
        setTitle(response.data.name || '');
      } catch (error) {
        console.error('Error fetching category:', error);
      }
    };
    fetchCategory();
  }, [id]);

  const handleChange = (e) => {
    setTitle(e.target.value);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      await axios.put(`/category/${id}`, { name: title });
      navigate('/categories');
    } catch (error) {
      console.error('Error updating category:', error);
    }
  };

  return (
    <Box sx={{ maxWidth: 800, mx: 'auto', mt: 4 }}>
      <Paper sx={{ p: 4 }}>
        <Typography variant="h4" gutterBottom>
          Edit Category
        </Typography>
        <form onSubmit={handleSubmit}>
          <Grid container spacing={3}>
            <Grid item xs={12}>
              <TextField
                required
                fullWidth
                label="Category Name"
                name="name"
                value={title}
                onChange={handleChange}
              />
            </Grid>
            <Grid item xs={12}>
              <Button
                type="submit"
                variant="contained"
                color="primary"
                size="large"
                sx={{ mt: 2, mr: 2 }}
              >
                Save Changes
              </Button>
              <Button
                variant="outlined"
                color="secondary"
                size="large"
                sx={{ mt: 2 }}
                onClick={() => navigate('/categories')}
              >
                Cancel
              </Button>
            </Grid>
          </Grid>
        </form>
      </Paper>
    </Box>
  );
} 