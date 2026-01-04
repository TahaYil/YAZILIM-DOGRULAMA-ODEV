import React, { useState } from 'react';
import { Box, TextField, Button, Typography, Paper, Snackbar, Alert } from '@mui/material';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';

export default function CategoryPage() {
  const navigate = useNavigate();
  const [formData, setFormData] = useState({
    name: '',
  });
  const [notification, setNotification] = useState({
    open: false,
    message: '',
    severity: 'success'
  });

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prevState => ({
      ...prevState,
      [name]: value
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    const token = localStorage.getItem('token'); 

    try {
      const response = await axios.post(
        'http://localhost:8080/category',
        formData,
        {
          headers: {
            Authorization: `Bearer ${token}`, 
          }
        }
      );
      console.log('Category created:', response.data);
      
      setNotification({
        open: true,
        message: 'Kategori başarıyla eklendi!',
        severity: 'success'
      });

      setFormData({ name: '' });

      setTimeout(() => {
        navigate('/categories');
      }, 1500);

    } catch (error) {
      console.error('Error creating category:', error);
      setNotification({
        open: true,
        message: error.response?.data?.message || 'Kategori eklenirken bir hata oluştu.',
        severity: 'error'
      });
    }
  };

  const handleCloseNotification = () => {
    setNotification(prev => ({ ...prev, open: false }));
  };

  return (
    <Box sx={{ maxWidth: 800, mx: 'auto', mt: 4 }}>
      <Paper sx={{ p: 4 }}>
        <Typography variant="h4" gutterBottom>
          Add New Category
        </Typography>
        <form onSubmit={handleSubmit}>
          <TextField
            required
            fullWidth
            label="Category Name"
            name="name"
            value={formData.name}
            onChange={handleChange}
            sx={{ mb: 2 }}
          />
          <Button
            type="submit"
            variant="contained"
            color="primary"
            size="large"
          >
            Add Category
          </Button>
        </form>
      </Paper>
      <Snackbar 
        open={notification.open} 
        autoHideDuration={3000} 
        onClose={handleCloseNotification}
        anchorOrigin={{ vertical: 'top', horizontal: 'center' }}
      >
        <Alert 
          onClose={handleCloseNotification} 
          severity={notification.severity}
          sx={{ width: '100%' }}
        >
          {notification.message}
        </Alert>
      </Snackbar>
    </Box>
  );
} 