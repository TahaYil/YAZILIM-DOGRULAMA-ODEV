import React, { useState } from 'react';
import {
  Box,
  Paper,
  TextField,
  Button,
  Typography,
  Container,
  Alert,
  InputAdornment,
  IconButton,
  useTheme,
} from '@mui/material';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import Visibility from '@mui/icons-material/Visibility';
import VisibilityOff from '@mui/icons-material/VisibilityOff';
import AdminPanelSettingsIcon from '@mui/icons-material/AdminPanelSettings';

const Login = () => {
  const theme = useTheme();
  const navigate = useNavigate();
  const [formData, setFormData] = useState({
    email: '',
    password: '',
  });
  const [error, setError] = useState('');
  const [showPassword, setShowPassword] = useState(false);
  const [loading, setLoading] = useState(false);

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value,
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      const response = await axios.post('http://localhost:8080/auth/login', formData);
      const token = response.data.token;

      const payloadBase64 = token.split('.')[1];
      const payloadJson = atob(payloadBase64);
      const payload = JSON.parse(payloadJson);
      const roles = payload.roles || [];
      const hasAdminRole = roles.includes("ROLE_ADMIN");

      if (!hasAdminRole) {
        setError("Admin rolü olmayan kullanıcılar admin sistemine erişemez.");
        return;
      }

      localStorage.setItem('token', token);
      
      // Token güncellendiğini App component'ine bildir
      window.dispatchEvent(new Event('tokenUpdated'));
      
      setTimeout(() => {
        navigate('/products', { replace: true });
      }, 100);
    } catch (err) {
      setError(err.response?.data?.message || 'Giriş yapılamadı. Lütfen tekrar deneyin.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <Box
      sx={{
        minHeight: '100vh',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        background: 'linear-gradient(135deg, #1a237e 0%, #0d47a1 100%)',
        padding: 2,
      }}
    >
      <Container component="main" maxWidth="xs">
        <Paper
          elevation={6}
          sx={{
            padding: 4,
            display: 'flex',
            flexDirection: 'column',
            alignItems: 'center',
            width: '100%',
            backgroundColor: 'rgba(255, 255, 255, 0.95)',
            borderRadius: 3,
            backdropFilter: 'blur(10px)',
            boxShadow: '0 8px 32px rgba(0, 0, 0, 0.1)',
          }}
        >
          <Box
            sx={{
              width: 70,
              height: 70,
              backgroundColor: theme.palette.primary.main,
              borderRadius: '50%',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              marginBottom: 3,
              boxShadow: '0 4px 12px rgba(0, 0, 0, 0.15)',
            }}
          >
            <AdminPanelSettingsIcon sx={{ color: 'white', fontSize: 35 }} />
          </Box>
          
          <Typography 
            component="h1" 
            variant="h4" 
            sx={{ 
              mb: 1,
              fontWeight: 600,
              color: theme.palette.primary.main,
              textAlign: 'center',
            }}
          >
            BUCATA Admin
          </Typography>

          <Typography 
            variant="body1" 
            sx={{ 
              mb: 4,
              color: 'text.secondary',
              textAlign: 'center',
            }}
          >
            Yönetici paneline hoş geldiniz
          </Typography>

          {error && (
            <Alert 
              severity="error" 
              sx={{ 
                width: '100%', 
                mb: 3,
                borderRadius: 2,
                '& .MuiAlert-icon': {
                  alignItems: 'center',
                },
              }}
            >
              {error}
            </Alert>
          )}

          <Box component="form" onSubmit={handleSubmit} sx={{ width: '100%' }}>
            <TextField
              margin="normal"
              required
              fullWidth
              id="email"
              label="E-posta Adresi"
              name="email"
              autoComplete="email"
              autoFocus
              value={formData.email}
              onChange={handleChange}
              sx={{ 
                mb: 2,
                '& .MuiOutlinedInput-root': {
                  borderRadius: 2,
                  '&:hover fieldset': {
                    borderColor: theme.palette.primary.main,
                  },
                },
              }}
            />
            <TextField
              margin="normal"
              required
              fullWidth
              name="password"
              label="Şifre"
              type={showPassword ? 'text' : 'password'}
              id="password"
              autoComplete="current-password"
              value={formData.password}
              onChange={handleChange}
              sx={{ 
                mb: 3,
                '& .MuiOutlinedInput-root': {
                  borderRadius: 2,
                  '&:hover fieldset': {
                    borderColor: theme.palette.primary.main,
                  },
                },
              }}
              InputProps={{
                endAdornment: (
                  <InputAdornment position="end">
                    <IconButton
                      aria-label="toggle password visibility"
                      onClick={() => setShowPassword(!showPassword)}
                      edge="end"
                      sx={{ color: theme.palette.primary.main }}
                    >
                      {showPassword ? <VisibilityOff /> : <Visibility />}
                    </IconButton>
                  </InputAdornment>
                ),
              }}
            />
            <Button
              type="submit"
              fullWidth
              variant="contained"
              size="large"
              disabled={loading}
              sx={{
                mt: 2,
                mb: 2,
                py: 1.5,
                borderRadius: 2,
                textTransform: 'none',
                fontSize: '1.1rem',
                fontWeight: 600,
                boxShadow: '0 4px 12px rgba(0, 0, 0, 0.15)',
                '&:hover': {
                  boxShadow: '0 6px 16px rgba(0, 0, 0, 0.2)',
                },
              }}
            >
              {loading ? 'Giriş Yapılıyor...' : 'Giriş Yap'}
            </Button>
          </Box>
        </Paper>
      </Container>
    </Box>
  );
};

export default Login;