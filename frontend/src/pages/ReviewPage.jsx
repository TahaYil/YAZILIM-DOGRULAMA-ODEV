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

export default function ReviewPage() {
  const navigate = useNavigate();
  const [reviews, setReviews] = useState([]);

  useEffect(() => {
    const fetchCategories = async () => {
      try {
        const response = await axios.get('http://localhost:8080/review');
        setReviews(response.data);
      } catch (error) {
        console.error('Error fetching categories:', error);
      }
    };
    fetchCategories();
  }, []);

 

  const handleDelete = async (reviewId) => {
    try {
      await axios.delete(`http://localhost:8080/review/${reviewId}`);
      setReviews(prev => prev.filter(cat => cat.id !== reviewId));
    } catch (error) {
      !alert("bu kategoride ürün bulunduğundan silinimez");
      console.error('Error deleting category:', error);
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
              <TableCell>user ID</TableCell>
              <TableCell>rating</TableCell>
              <TableCell>product ID</TableCell>

              <TableCell>Actions</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {reviews.map((review) => (
              <TableRow key={review.id}>
                <TableCell>{review.id}</TableCell>
                <TableCell>{review.userId}</TableCell>
                <TableCell>{review.rating}</TableCell>
                <TableCell>{review.productId}</TableCell>


                <TableCell>
                  <IconButton 
                    color="error" 
                    onClick={() => handleDelete(review.id)}
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