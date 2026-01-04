import React, { useState, useEffect } from 'react';
import {
  Box,
  Grid,
  Card,
  CardContent,
  CardMedia,
  Typography,
  IconButton,
  CardActions,
} from '@mui/material';
import EditIcon from '@mui/icons-material/Edit';
import DeleteIcon from '@mui/icons-material/Delete';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';

// Base64 encoded placeholder image
const PLACEHOLDER_IMAGE = 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMTgwIiBoZWlnaHQ9IjE4MCIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj48cmVjdCB3aWR0aD0iMTgwIiBoZWlnaHQ9IjE4MCIgZmlsbD0iI2Y1ZjVmNSIvPjx0ZXh0IHg9IjUwJSIgeT0iNTAlIiBmb250LWZhbWlseT0iQXJpYWwiIGZvbnQtc2l6ZT0iMTYiIGZpbGw9IiM5OTkiIHRleHQtYW5jaG9yPSJtaWRkbGUiIGR5PSIuM2VtIj5SZXNpbSBZb2s8L3RleHQ+PC9zdmc+';

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

export default function ProductsPage() {
  const navigate = useNavigate();
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [categoryMap, setCategoryMap] = useState({});

  useEffect(() => {
    const fetchProducts = async () => {
      try {
        const [productRes, categoryRes] = await Promise.all([
          axios.get('http://localhost:8080/product/all'),
          axios.get('http://localhost:8080/category'), 
        ]);
        setProducts(productRes.data);

        const categoryMap = categoryRes.data.reduce((acc, cat) => {
          acc[cat.id] = cat.name;
          return acc;
        }, {});
        setCategoryMap(categoryMap);

      } catch (err) {
        console.error('Ürünler alınamadı:', err);
      } finally {
        setLoading(false);
      }
    };
    fetchProducts();
  }, []);

  const handleEdit = (productId) => {
    navigate(`/product-duzen/${productId}`);
  };

  const handleDelete = async (productId) => {
    try {
      await axios.delete(`http://localhost:8080/product/${productId}`);
      setProducts((prev) => prev.filter((p) => p.id !== productId));
    } catch (err) {
      console.error('Silme işlemi başarısız:', err);
    }
  };

  const getImageUrl = (productId) => {
    const token = localStorage.getItem('token');
    return `http://localhost:8080/product/${productId}/image?token=${token}`;
  };

  if (loading) return <Typography>Loading...</Typography>;

  return (
    <Box sx={{ p: 3 }}>
      <Typography variant="h4" gutterBottom>
        Products
      </Typography>
      <Grid container spacing={3}>
        {products.map((product) => (
          <Grid item xs={12} sm={6} md={4} lg={3} key={product.id}>
            <Card>
              <CardMedia
                component="img"
                height="180"
                image={getImageUrl(product.id)}
                alt={product.name}
                onError={(e) => {
                  e.target.onerror = null;
                  e.target.src = PLACEHOLDER_IMAGE;
                }}
              />
              <CardContent>
                <Typography variant="h6" gutterBottom>
                  {product.name}
                </Typography>
                <Typography variant="body2" color="text.secondary" gutterBottom>
                  {product.description || 'No description'}
                </Typography>
                <Typography variant="body1">Price: {product.price} ₺</Typography>
                <Typography variant="body2">Stock: {product.quantity}</Typography>
                {product.sizeStocks && (
                  <Typography variant="body2" color="text.secondary">
                    {Object.entries(product.sizeStocks).map(([size, stock]) => `${size}: ${stock}`).join(', ')}
                  </Typography>
                )}
                <Typography variant="body2">Category: {product.categoryIds.map(id => categoryMap[id]).filter(Boolean).join(', ')}</Typography>
              </CardContent>
              <CardActions>
                <IconButton color="primary" onClick={() => handleEdit(product.id)}>
                  <EditIcon />
                </IconButton>
                <IconButton color="error" onClick={() => handleDelete(product.id)}>
                  <DeleteIcon />
                </IconButton>
              </CardActions>
            </Card>
          </Grid>
        ))}
      </Grid>
    </Box>
  );
} 