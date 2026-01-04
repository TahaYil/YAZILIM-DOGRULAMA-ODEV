import React from "react";
import {
  AppBar,
  Box,
  CssBaseline,
  Drawer,
  List,
  ListItem,
  ListItemButton,
  ListItemIcon,
  ListItemText,
  Toolbar,
  Typography,
  IconButton,
} from "@mui/material";
import MenuIcon from "@mui/icons-material/Menu";
import PeopleIcon from "@mui/icons-material/People";
import ShoppingCartIcon from "@mui/icons-material/ShoppingCart";
import StorefrontIcon from "@mui/icons-material/Storefront";
import AddCircleIcon from "@mui/icons-material/AddCircle";
import CategoryIcon from "@mui/icons-material/Category";
import ReviewIcon from "@mui/icons-material/Chat";

import { Link, useLocation, useNavigate } from "react-router-dom";

const drawerWidth = 240;

export default function SideBar({ children }) {
  const location = useLocation(); // Aktif sayfayı almak için
  const navigate = useNavigate();

  const menuItems = [
    { text: "Products", icon: <StorefrontIcon />, path: "/products" },
    { text: "Categories", icon: <CategoryIcon />, path: "/categories" },
    { text: "Users", icon: <PeopleIcon />, path: "/users" },
    { text: "Orders", icon: <ShoppingCartIcon />, path: "/orders" },
    { text: "Add Product", icon: <AddCircleIcon />, path: "/product-page" },
    { text: "Add Category", icon: <CategoryIcon />, path: "/category-page" },
    { text: "Reviews", icon: <ReviewIcon />, path: "/review-page" },

  ];

  const handleLogout = () => {
    localStorage.removeItem("token");
    // Token silindiğini App component'ine bildir
    window.dispatchEvent(new Event('tokenUpdated'));
    navigate("/auth/login");
  };

  return (
    <Box sx={{ display: "flex" }}>
      <CssBaseline />

      {/* Üst Menü */}
      <AppBar
        position="fixed"
        sx={{ zIndex: (theme) => theme.zIndex.drawer + 1 }}
      >
        <Toolbar>
          
          <Typography variant="h6" noWrap>
            Admin Panel
          </Typography>
        </Toolbar>
      </AppBar>

      {/* Sidebar */}
      <Drawer
        variant="permanent"
        sx={{
          width: drawerWidth,
          flexShrink: 0,
          [`& .MuiDrawer-paper`]: {
            width: drawerWidth,
            boxSizing: "border-box",
          },
        }}
      >
        <Toolbar />
        <List>
          {menuItems.map((item) => (
            <ListItem key={item.text} disablePadding>
              <ListItemButton
                component={Link}
                to={item.path}
                sx={{
                  backgroundColor:
                    location.pathname === item.path ? "#ddd" : "inherit",
                }}
              >
                <ListItemIcon>{item.icon}</ListItemIcon>
                <ListItemText primary={item.text} />
              </ListItemButton>
            </ListItem>
          ))}
          <ListItem disablePadding>
            <ListItemButton onClick={handleLogout}>
              <ListItemIcon>
                <MenuIcon />
              </ListItemIcon>
              <ListItemText primary="Çıkış Yap" />
            </ListItemButton>
          </ListItem>
        </List>
      </Drawer>
      
      {/* Sayfa İçeriği */}
      <Box
        component="main"
        sx={{ flexGrow: 1, p: 3, mt: "64px" }}
      >
        {children}
      </Box>
    </Box>
  );
}
