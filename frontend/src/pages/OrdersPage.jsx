import React, { useState, useEffect } from 'react';
import { 
  Typography, 
  Table, 
  Button, 
  Box,
  TextField,
  Select,
  MenuItem,
  FormControl,
  InputLabel,
  Grid,
  IconButton,
  Tooltip
} from '@mui/material';
import TableBody from '@mui/material/TableBody';
import TableCell from '@mui/material/TableCell';
import TableContainer from '@mui/material/TableContainer';
import TableHead from '@mui/material/TableHead';
import TableRow from '@mui/material/TableRow';
import Paper from '@mui/material/Paper';
import { useNavigate } from 'react-router-dom';
import EditIcon from '@mui/icons-material/Edit';
import axios from 'axios';
import ArrowUpwardIcon from '@mui/icons-material/ArrowUpward';
import ArrowDownwardIcon from '@mui/icons-material/ArrowDownward';

export default function OrdersPage() {
  const navigate = useNavigate();
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [dateFilter, setDateFilter] = useState('');
  const [stateFilter, setStateFilter] = useState('');
  const [sortConfig, setSortConfig] = useState({
    key: null,
    direction: 'asc'
  });

  useEffect(() => {
    const fetchOrders = async () => {
      try {
        const response = await axios.get('http://localhost:8080/ordered');
        setOrders(response.data);
        setLoading(false);
      } catch (err) {
        setError('Error fetching orders');
        setLoading(false);
        console.error('Error fetching orders:', err);
      }
    };

    fetchOrders();
  }, []);

  const handleEditOrder = (orderId) => {
    navigate(`/orders/${orderId}`);
  };

  const getStateColor = (state) => {
    switch (state) {
      case 'PENDING':
        return 'warning.main';
      case 'PROCESSING':
        return 'info.main';
      case 'SHIPPED':
        return 'primary.main';
      case 'DELIVERED':
        return 'success.main';
      case 'CANCELLED':
        return 'error.main';
      default:
        return 'text.primary';
    }
  };

  const handleSort = (key) => {
    let direction = 'asc';
    if (sortConfig.key === key && sortConfig.direction === 'asc') {
      direction = 'desc';
    }
    setSortConfig({ key, direction });
  };

  const sortedAndFilteredOrders = React.useMemo(() => {
    let result = [...orders];

    // Apply filters
    if (dateFilter) {
      result = result.filter(order => 
        new Date(order.date).toLocaleDateString().includes(dateFilter)
      );
    }
    if (stateFilter) {
      result = result.filter(order => 
        order.state.toLowerCase() === stateFilter.toLowerCase()
      );
    }

    // Apply sorting
    if (sortConfig.key) {
      result.sort((a, b) => {
        if (sortConfig.key === 'date') {
          const dateA = new Date(a.date);
          const dateB = new Date(b.date);
          return sortConfig.direction === 'asc' ? dateA - dateB : dateB - dateA;
        } else if (sortConfig.key === 'id') {
          return sortConfig.direction === 'asc' ? a.id - b.id : b.id - a.id;
        }
        return 0;
      });
    }

    return result;
  }, [orders, dateFilter, stateFilter, sortConfig]);

  if (loading) {
    return (
      <Box sx={{ p: 3 }}>
        <Typography>Loading orders...</Typography>
      </Box>
    );
  }

  if (error) {
    return (
      <Box sx={{ p: 3 }}>
        <Typography color="error">{error}</Typography>
      </Box>
    );
  }

  return (
    <Box sx={{ p: 3 }}>
      <Typography variant="h4" gutterBottom>
        Orders
      </Typography>
      
      <Grid container spacing={2} sx={{ mb: 3 }}>
        <Grid item xs={12} sm={6} md={4}>
          <TextField
            label="Filter by Date"
            variant="outlined"
            size="small"
            value={dateFilter}
            onChange={(e) => setDateFilter(e.target.value)}
            fullWidth
            placeholder="MM/DD/YYYY"
          />
        </Grid>
        <Grid item xs={12} sm={6} md={4}>
          <FormControl fullWidth size="small">
            <InputLabel>Filter by State</InputLabel>
            <Select
              value={stateFilter}
              label="Filter by State"
              onChange={(e) => setStateFilter(e.target.value)}
            >
              <MenuItem value="">All</MenuItem>
              <MenuItem value="PENDING">Pending</MenuItem>
              <MenuItem value="PROCESSING">Processing</MenuItem>
              <MenuItem value="SHIPPED">Shipped</MenuItem>
              <MenuItem value="DELIVERED">Delivered</MenuItem>
              <MenuItem value="CANCELLED">Cancelled</MenuItem>
            </Select>
          </FormControl>
        </Grid>
      </Grid>
      
      <TableContainer component={Paper}>
        <Table sx={{ minWidth: 650 }} aria-label="orders table">
          <TableHead>
            <TableRow>
              <TableCell>
                Order ID
                <Tooltip title="Sort by ID">
                  <IconButton size="small" onClick={() => handleSort('id')}>
                    {sortConfig.key === 'id' ? (
                      sortConfig.direction === 'asc' ? <ArrowUpwardIcon /> : <ArrowDownwardIcon />
                    ) : <ArrowUpwardIcon />}
                  </IconButton>
                </Tooltip>
              </TableCell>
              <TableCell>User ID</TableCell>
              <TableCell>
                Date
                <Tooltip title="Sort by Date">
                  <IconButton size="small" onClick={() => handleSort('date')}>
                    {sortConfig.key === 'date' ? (
                      sortConfig.direction === 'asc' ? <ArrowUpwardIcon /> : <ArrowDownwardIcon />
                    ) : <ArrowUpwardIcon />}
                  </IconButton>
                </Tooltip>
              </TableCell>
              <TableCell>State</TableCell>
              <TableCell align="center">Actions</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {sortedAndFilteredOrders.map((order) => (
              <TableRow
                key={order.id}
                sx={{ '&:last-child td, &:last-child th': { border: 0 } }}
              >
                <TableCell component="th" scope="row">
                  {order.id}
                </TableCell>
                <TableCell>{order.userId}</TableCell>
                <TableCell>{new Date(order.date).toLocaleDateString()}</TableCell>
                <TableCell>
                  <Typography
                    sx={{
                      color: getStateColor(order.state),
                      fontWeight: 'medium'
                    }}
                  >
                    {order.state}
                  </Typography>
                </TableCell>
                <TableCell>
                <Button
                variant="outlined"
                color="secondary"
                size="large"
                sx={{ mt: 2  }}
                onClick={() => navigate(`/orders/${order.id}`)}
              >
                Edit
              </Button>
                </TableCell>
                
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>
    </Box>
  );
}
