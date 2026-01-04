import { Routes, Route, Navigate } from "react-router-dom";
import React, { useEffect, useState } from "react";
import SideBar from "./components/SideBar";
import Users from "./pages/UsersPage";
import Orders from "./pages/OrdersPage";
import ProductsPage from "./pages/ProductsPage";
import ProductPage from "./pages/ProductPage";
import CategoryPage from "./pages/CategoryPage";
import ProductDuzen from "./pages/ProductDuzen";
import CategoriesPage from "./pages/CategoriesPage";
import CategoryDuzen from "./pages/CategoryDuzen";
import UserDuzen from "./pages/UserDuzen";
import OrderDuzen from "./pages/OrderDuzen";
import Login from "./pages/Login";
import ReviewPage from "./pages/ReviewPage";
import axios from "axios";

axios.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem("token");
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

axios.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401 || error.response?.status === 403) {
      localStorage.removeItem("token");
      window.location.href = "/auth/login";
    }
    return Promise.reject(error);
  }
);

function ProtectedRoutes() {
  return (
    <SideBar>
      <Routes>
        <Route path="/" element={<ProductsPage />} />
        <Route path="/users" element={<Users />} />
        <Route path="/users/:id" element={<UserDuzen />} />
        <Route path="/products" element={<ProductsPage />} />
        <Route path="/categories" element={<CategoriesPage />} />
        <Route path="/orders" element={<Orders />} />
        <Route path="/orders/:id" element={<OrderDuzen />} />
        <Route path="/product-page" element={<ProductPage />} />
        <Route path="/category-page" element={<CategoryPage />} />
        <Route path="/product-duzen/:id" element={<ProductDuzen />} />
        <Route path="/category-duzen/:id" element={<CategoryDuzen />} />
        <Route path="/review-page" element={<ReviewPage />} />
      </Routes>
    </SideBar>
  );
}

function App() {
  const [token, setToken] = useState(localStorage.getItem("token"));

  useEffect(() => {
    // Token değişikliklerini dinle
    const handleTokenChange = () => {
      setToken(localStorage.getItem("token"));
    };

    // Custom event: token kaydedildiğinde
    window.addEventListener("tokenUpdated", handleTokenChange);
    
    // Storage event: başka tab'dan yapılan değişiklikler için
    window.addEventListener("storage", handleTokenChange);

    return () => {
      window.removeEventListener("tokenUpdated", handleTokenChange);
      window.removeEventListener("storage", handleTokenChange);
    };
  }, []);

  return (
    <Routes>
      <Route path="/auth/login" element={<Login />} />
      <Route
        path="/*"
        element={
          token ? <ProtectedRoutes /> : <Navigate to="/auth/login" replace />
        }
      />
    </Routes>
  );
}

export default App;