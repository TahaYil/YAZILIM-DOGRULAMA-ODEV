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
} from '@mui/material';

export default function UserDuzen() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [formData, setFormData] = useState({
    name: '',
    email: '',
    role: 'USER',
  });

  useEffect(() => {
    const fetchUser = async () => {
      try {
        const response = await axios.get(`http://localhost:8080/user/${id}`);
        setFormData({
          id:response.data.id,
          gender:response.data.gender,
          password:response.data.password,
          name: response.data.name,
          email: response.data.email,
          role: response.data.role,
        });
      } catch (error) {
        console.error('Error fetching user:', error);
      }
    };
    fetchUser();
  }, [id]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prevState => ({
      ...prevState,
      [name]: value
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      await axios.put(`http://localhost:8080/user/${id}`, formData);
      navigate('/users');
    } catch (error) {
      console.error('Error updating user:', error);
    }
  };

  return (
    <Box sx={{ maxWidth: 800, mx: 'auto', mt: 4 }}>
      <Paper sx={{ p: 4 }}>
        <Typography variant="h4" gutterBottom>
          Edit User
        </Typography>
        <form onSubmit={handleSubmit}>
          <Grid container spacing={3}>
            
            
            <Grid item xs={12}>
              <TextField
                required
                fullWidth
                label="Email"
                name="email"
                type="email"
                value={formData.email}
                onChange={handleChange}
              />
            </Grid>

            <Grid item xs={12}>
              <FormControl fullWidth>
                <InputLabel>Role</InputLabel>
                <Select
                  name="role"
                  value={formData.role}
                  label="Role"
                  onChange={handleChange}
                >
                  <MenuItem value="ADMIN">ADMIN</MenuItem>
                  <MenuItem value="USER">USER</MenuItem>
                </Select>
              </FormControl>
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
                onClick={() => navigate('/users')}
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