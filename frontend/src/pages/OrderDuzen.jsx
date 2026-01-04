import React, { useState, useEffect } from "react";
import { useParams, useNavigate } from "react-router-dom";
import axios from "axios";
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
} from "@mui/material";

export default function OrderDuzen() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [formData, setFormData] = useState({
    customerName: "",
    date: "",
    totalAmount: "",
    state: "",
    paymentMethod: "Credit Card",
    shippingAddress: "",
  });

  useEffect(() => {
    const fetchOrder = async () => {
      try {
        const response = await axios.get(`http://localhost:8080/ordered/${id}`);
        setFormData(response.data);
      } catch (error) {
        console.error("Error fetching order:", error);
      }
    };

    fetchOrder();
  }, [id]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prevState) => ({
      ...prevState,
      [name]: value,
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      await axios.put(`http://localhost:8080/ordered/${id}`, formData);
      navigate("/orders");
    } catch (error) {
      console.error("Error updating order:", error);
    }
  };

  return (
    <Box sx={{ maxWidth: 800, mx: "auto", mt: 4 }}>
      <Paper sx={{ p: 4 }}>
        <Typography variant="h4" gutterBottom>
          Edit Order
        </Typography>
        <form onSubmit={handleSubmit}>
          <Grid container spacing={3}>
            <Grid item xs={12}>
              <TextField
                required
                fullWidth
                label="Order Date"
                name="orderDate"
                type="date"
                value={formData.date}
                onChange={handleChange}
                InputLabelProps={{
                  shrink: true,
                }}
              />
            </Grid>

            <Grid item xs={12}>
              <FormControl fullWidth>
                <InputLabel>Status</InputLabel>
                <Select
                  name="state"
                  value={formData.state}
                  label="Status"
                  onChange={handleChange}
                >
                  <MenuItem value="PENDING">Pending</MenuItem>
                  <MenuItem value="PROCESSING">Processing</MenuItem>
                  <MenuItem value="SHIPPED">Shipped</MenuItem>
                  <MenuItem value="DELIVERED">Delivered</MenuItem>
                  <MenuItem value="CANCELLED">Cancelled</MenuItem>
                </Select>
              </FormControl>
            </Grid>

            <Grid item xs={12}>
              <TextField
                required
                fullWidth
                name="userId"
                label="User Id"
                multiline
                rows={1}
                value={formData.userId}
                onChange={handleChange}
                InputLabelProps={{
                  shrink: true,
                }}
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
                onClick={() => navigate("/orders")}
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
