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
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  Divider,
} from '@mui/material';

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

export default function ProductDuzen() {
  const { id } = useParams();
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
  const [loadingProduct, setLoadingProduct] = useState(true);
  const [currentImage, setCurrentImage] = useState(null);
  const [alert, setAlert] = useState({ open: false, message: '', severity: 'success' });

  useEffect(() => {
    const fetchCategories = async () => {
      setLoadingCategories(true);
      try {
        const res = await axios.get('http://localhost:8080/category');
        setCategories(res.data);
      } catch (err) {
        console.error('Kategori çekme hatası:', err);
        setCategories([]);
      } finally {
        setLoadingCategories(false);
      }
    };
    fetchCategories();
  }, []);

  useEffect(() => {
    const fetchProduct = async () => {
      setLoadingProduct(true);
      try {
        const res = await axios.get(`http://localhost:8080/product/${id}`);
        const product = res.data;
        setFormData({
          name: product.name || '',
          price: product.price || '',
          description: product.description || '',
          categories: product.categoryIds ? product.categoryIds.map(String) : [],
          image: null,
          sizeStocks: product.sizeStocks || {
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
        setCurrentImage(product.imageUrl);
      } catch (err) {
        console.error('Ürün çekme hatası:', err);
      } finally {
        setLoadingProduct(false);
      }
    };
    fetchProduct();
  }, [id]);

  const handleChange = (e) => {
    const { name, value, type, files } = e.target;
    if (type === 'file') {
      setFormData(prevState => ({
        ...prevState,
        image: files[0]
      }));
    } else if (name === 'categories') {
      setFormData(prevState => ({
        ...prevState,
        categories: Array.isArray(value) ? value : [value]
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

    const productData = {
      id: parseInt(id, 10),
      name: formData.name,
      price: formData.price,
      description: formData.description,
      categoryIds: formData.categories
        .map(id => parseInt(id, 10))
        .filter(id => !isNaN(id) && id !== null),
      sizeStocks: formData.sizeStocks
    };

    const data = new FormData();
    data.append('product', new Blob([JSON.stringify(productData)], { type: 'application/json' }));
    if (formData.image) {
      data.append('image', formData.image);
    }

    try {
      const response = await axios.put(`http://localhost:8080/product/${id}`, data, {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
      });
      
      if (response.status === 200) {
        navigate('/products');
      } else {
        console.error('Ürün güncellenemedi');
      }
    } catch (err) {
      console.error('Ürün güncelleme hatası:', err);
      if (err.response?.status === 404) {
        console.error('Ürün bulunamadı');
      }
    }
  };

  const handleDelete = async (id) => {
    if (window.confirm('Bu ürünü silmek istediğinizden emin misiniz?')) {
      try {
        await axios.delete(`http://localhost:8080/product/${id}`);
        setAlert({ open: true, message: 'Ürün başarıyla silindi', severity: 'success' });
      } catch (error) {
        let errorMessage = 'Ürün silinirken bir hata oluştu';
        if (error.response && error.response.data && error.response.data.message) {
          errorMessage = error.response.data.message;
        } else if (error.message) {
          errorMessage = error.message;
        }
        setAlert({ open: true, message: errorMessage, severity: 'error' });
      }
    }
  };

  if (loadingProduct) return <Typography>Loading...</Typography>;

  return (
    <Box sx={{ maxWidth: 800, mx: 'auto', mt: 4 }}>
      <Paper sx={{ p: 4 }}>
        <Typography variant="h4" gutterBottom>
          Edit Product
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
                  value={formData.categories}
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
              {currentImage && !formData.image && (
                <Box sx={{ mt: 2 }}>
                  <Typography variant="body2" gutterBottom>Current Image:</Typography>
                  <img 
                    src={currentImage} 
                    alt="Current product" 
                    style={{ maxWidth: '200px', maxHeight: '200px' }} 
                  />
                </Box>
              )}
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
                sx={{ mt: 2, mr: 2 }}
              >
                Save Changes
              </Button>
              <Button
                variant="outlined"
                color="secondary"
                size="large"
                sx={{ mt: 2 }}
                onClick={() => navigate('/products')}
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