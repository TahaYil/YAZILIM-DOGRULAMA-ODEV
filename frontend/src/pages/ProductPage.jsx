import React, { useState, useEffect } from 'react';
import axios from 'axios';
import {
  Box,
  TextField,
  Button,
  Typography,
  Paper,
  Grid,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  Divider,
  Snackbar,
  Alert,
} from '@mui/material';
import { useNavigate } from 'react-router-dom';

axios.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

export default function ProductPage() {
  const navigate = useNavigate();
  const [formData, setFormData] = useState({
    name: '',
    price: '',
    description: '',
    categories: [],
    image: null,
    sizeStocks: {
      XS: 0,
      S: 0,
      M: 0,
      L: 0,
      XL: 0,
      XXL: 0,
      '3XL': 0,
      '4XL': 0,
      '5XL': 0,
      '6XL': 0
    }
  });
  const [categories, setCategories] = useState([]);
  const [loadingCategories, setLoadingCategories] = useState(false);
  const [notification, setNotification] = useState({
    open: false,
    message: '',
    severity: 'success'
  });

  useEffect(() => {
    const fetchCategories = async () => {
      setLoadingCategories(true);
      try {
        const res = await axios.get('http://localhost:8080/category');
        setCategories(res.data);
      } catch (err) {
        setCategories([]);
      } finally {
        setLoadingCategories(false);
      }
    };
    fetchCategories();
  }, []);

  const handleChange = (e) => {
    const { name, value, type, files } = e.target;
    if (type === 'file') {
      setFormData(prevState => ({
        ...prevState,
        image: files[0]
      }));
    } else if (name === 'categories') {
      const selectedCategories = value.map(id => Number(id));
      setFormData(prevState => ({
        ...prevState,
        categories: selectedCategories
      }));
    } else {
      setFormData(prevState => ({
        ...prevState,
        [name]: value
      }));
    }
  };

  const handleSizeStockChange = (size, value) => {
    setFormData(prevState => ({
      ...prevState,
      sizeStocks: {
        ...prevState.sizeStocks,
        [size]: parseInt(value) || 0
      }
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    try {
      const product = {
        name: formData.name,
        price: parseFloat(formData.price),
        description: formData.description,
        categoryIds: formData.categories.map(id => Number(id)),
        sizeStocks: formData.sizeStocks
      };

      const formDataToSend = new FormData();
      formDataToSend.append('product', JSON.stringify(product));
      
      if (formData.image) {
        formDataToSend.append('image', formData.image);
      }

      const response = await axios.post('http://localhost:8080/product', formDataToSend, {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
      });

      console.log('Product added successfully:', response.data);
      
  
      setNotification({
        open: true,
        message: 'Ürün başarıyla eklendi!',
        severity: 'success'
      });

     
      setFormData({
        name: '',
        price: '',
        description: '',
        categories: [],
        image: null,
        sizeStocks: {
          XS: 0,
          S: 0,
          M: 0,
          L: 0,
          XL: 0,
          XXL: 0,
          '3XL': 0,
          '4XL': 0,
          '5XL': 0,
          '6XL': 0
        }
      });

      setTimeout(() => {
        navigate('/products');
      }, 1500);

    } catch (err) {
      console.error('Product addition error:', err);
      setNotification({
        open: true,
        message: err.response?.data?.message || 'Ürün eklenirken bir hata oluştu.',
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
          Add New Product
        </Typography>
        <form onSubmit={handleSubmit}>
          <Grid container spacing={3}>
            <Grid item xs={12}>
              <TextField
                required
                fullWidth
                label="Product Name"
                name="name"
                value={formData.name}
                onChange={handleChange}
              />
            </Grid>
            <Grid item xs={12} sm={6}>
              <TextField
                required
                fullWidth
                label="Price"
                name="price"
                type="number"
                value={formData.price}
                onChange={handleChange}
                InputProps={{
                  startAdornment: '₺',
                }}
              />
            </Grid>
            <Grid item xs={12}>
              <FormControl fullWidth>
                <InputLabel>Categories</InputLabel>
                <Select
                  name="categories"
                  multiple
                  value={formData.categories.map(String)}
                  label="Categories"
                  onChange={handleChange}
                  renderValue={(selected) =>
                    categories
                      .filter(cat => selected.includes(String(cat.id)))
                      .map(cat => cat.name)
                      .join(', ')
                  }
                  disabled={loadingCategories}
                >
                  {loadingCategories ? (
                    <MenuItem disabled>Loading...</MenuItem>
                  ) : (
                    categories.map((cat) => (
                      <MenuItem key={cat.id} value={String(cat.id)}>
                        {cat.name}
                      </MenuItem>
                    ))
                  )}
                </Select>
              </FormControl>
            </Grid>
            <Grid item xs={12}>
              <TextField
                fullWidth
                label="Description"
                name="description"
                multiline
                rows={4}
                value={formData.description}
                onChange={handleChange}
              />
            </Grid>

            <Grid item xs={12}>
              <Divider sx={{ my: 2 }}>
                <Typography variant="h6">Size Stock Management</Typography>
              </Divider>
            </Grid>

            {Object.entries(formData.sizeStocks).map(([size, stock]) => (
              <Grid item xs={6} sm={4} md={3} key={size}>
                <TextField
                  fullWidth
                  label={`${size} Stock`}
                  type="number"
                  value={stock}
                  onChange={(e) => handleSizeStockChange(size, e.target.value)}
                  InputProps={{
                    inputProps: { min: 0 }
                  }}
                />
              </Grid>
            ))}

            <Grid item xs={12}>
              <Button
                variant="contained"
                component="label"
                sx={{ mb: 2 }}
              >
                Upload Image
                <input
                  type="file"
                  accept="image/*"
                  hidden
                  name="image"
                  onChange={handleChange}
                />
              </Button>
              {formData.image && (
                <Typography variant="body2">Selected: {formData.image.name}</Typography>
              )}
            </Grid>
            <Grid item xs={12}>
              <Button
                type="submit"
                variant="contained"
                color="primary"
                size="large"
                sx={{ mt: 2 }}
              >
                Add Product
              </Button>
            </Grid>
          </Grid>
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
