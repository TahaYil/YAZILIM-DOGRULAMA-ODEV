import React, { useState, useEffect } from 'react';
import {
  Box,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  IconButton,
  Typography,
} from '@mui/material';
import EditIcon from '@mui/icons-material/Edit';
import DeleteIcon from '@mui/icons-material/Delete';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';

export default function CategoriesPage() {
  const navigate = useNavigate();
  const [categories, setCategories] = useState([]);

  useEffect(() => {
    const fetchCategories = async () => {
  try {
    const token = localStorage.getItem('token');

    const response = await axios.get('http://localhost:8080/category', {
      headers: {
        Authorization: `Bearer ${token}`
      }
    });
    setCategories(response.data);
  } catch (error) {
    if (error.response) {
      console.error('API error:', error.response.status, error.response.data);
    } else {
      console.error('Unknown error:', error);
    }
  }
};

    fetchCategories();
  }, []);

  const handleEdit = (categoryId) => {
    navigate(`/category-duzen/${categoryId}`);
  };

  const handleDelete = async (categoryId) => {
    try {
      const token = localStorage.getItem('token');
      
      // Get product count for this category
      const response = await axios.get(`http://localhost:8080/category/${categoryId}/products/count`, {
        headers: {
          Authorization: `Bearer ${token}`
        }
      });
      
      const productCount = response.data;
      
      if (productCount > 0) {
        const confirmMessage = `Bu kategoride ${productCount} ürün bulunmaktadır. Kategoriyi silmek istediğinizden emin misiniz? Bu işlem tüm ürünleri de silecektir.`;
        if (!window.confirm(confirmMessage)) {
          return;
        }
      }

      await axios.delete(`http://localhost:8080/category/${categoryId}`, {
        headers: {
          Authorization: `Bearer ${token}`
        }
      });

      setCategories(prev => prev.filter(cat => cat.id !== categoryId));
    } catch (error) {
      console.error('Error deleting category:', error);
      alert(error.response?.data?.message || 'Kategori silinirken bir hata oluştu.');
    }
  };

  return (
    <Box sx={{ p: 3 }}>
      <Typography variant="h4" gutterBottom>
        Categories
      </Typography>
      <TableContainer component={Paper}>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell>ID</TableCell>
              <TableCell>Name</TableCell>
              <TableCell>Actions</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {categories.map((category) => (
              <TableRow key={category.id}>
                <TableCell>{category.id}</TableCell>
                <TableCell>{category.name}</TableCell>
                <TableCell>
                  <IconButton 
                    color="primary" 
                    onClick={() => handleEdit(category.id)}
                    aria-label="edit"
                  >
                    <EditIcon />
                  </IconButton>
                  <IconButton 
                    color="error" 
                    onClick={() => handleDelete(category.id)}
                    aria-label="delete"
                  >
                    <DeleteIcon />
                  </IconButton>
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>
    </Box>
  );
}
