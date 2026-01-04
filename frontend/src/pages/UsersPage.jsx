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
  Chip,
  TextField,
} from '@mui/material';
import EditIcon from '@mui/icons-material/Edit';
import DeleteIcon from '@mui/icons-material/Delete';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';

export default function UsersPage() {
  const navigate = useNavigate();
  const [users, setUsers] = useState([]);
  const [searchId, setSearchId] = useState('');

  useEffect(() => {
    axios.get('http://localhost:8080/user/all')
      .then(response => setUsers(response.data))
      .catch(error => console.error('Error fetching users:', error));
  }, []);

  const filteredUsers = users.filter(user => 
    searchId === '' || user.id.toString().includes(searchId)
  );

  const handleEdit = (userId) => {
    navigate(`/users/${userId}`);
  };

  const handleDelete = async (userId) => {
    try {
      await axios.delete(`http://localhost:8080/user/${userId}`);
      setUsers(users.filter(user => user.id !== userId));
    } catch (error) {
      !alert("kullanıcının siparişi bulunduğundan silinemez")
      console.error('Error deleting user:', error);
    }
  };

  const getRoleColor = (role) => {
    return role === 'Admin' ? 'primary' : 'default';
  };

  return (
    <Box sx={{ p: 3 }}>
      <Typography variant="h4" gutterBottom>
        Users
      </Typography>
      <Box sx={{ mb: 3 }}>
        <TextField
          label="Search by ID"
          variant="outlined"
          size="small"
          value={searchId}
          onChange={(e) => setSearchId(e.target.value)}
          sx={{ width: 200 }}
        />
      </Box>
      <TableContainer component={Paper}>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell>ID</TableCell>
              
              <TableCell>Email</TableCell>
              <TableCell>Role</TableCell>
              
              <TableCell>Actions</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {filteredUsers.map((user) => (
              <TableRow key={user.id}>
                <TableCell>{user.id}</TableCell>
                
                <TableCell>{user.email}</TableCell>
                <TableCell>
                  <Chip 
                    label={user.role} 
                    color={getRoleColor(user.role)}
                    size="small"
                  />
                </TableCell>
                
                <TableCell>
                  <IconButton 
                    color="primary" 
                    onClick={() => handleEdit(user.id)}
                    aria-label="edit"
                  >
                    <EditIcon />
                  </IconButton>
                  <IconButton 
                    color="error" 
                    onClick={() => handleDelete(user.id)}
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
