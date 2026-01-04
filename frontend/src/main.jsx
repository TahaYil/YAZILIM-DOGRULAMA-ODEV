import { createRoot } from "react-dom/client";
import "./index.css";
import App from "./App.jsx";
import { BrowserRouter } from "react-router-dom";
import axios from "axios";

// Token süresi dolmuşsa otomatik logout
const token = localStorage.getItem("token");
if (token) {
  try {
    const payload = JSON.parse(atob(token.split(".")[1]));
    if (payload.exp * 1000 < Date.now()) {
      localStorage.removeItem("token");
      window.location.href = "/auth/login";
    }
  } catch (e) {
    localStorage.removeItem("token");
    window.location.href = "/auth/login";
  }
}

// Axios interceptor: 401/403 olursa otomatik logout
axios.interceptors.response.use(
  response => response,
  error => {
    if (error.response && (error.response.status === 401 || error.response.status === 403)) {
      localStorage.removeItem("token");
      window.location.href = "/auth/login";
    }
    return Promise.reject(error);
  }
);

createRoot(document.getElementById("root")).render(
  <BrowserRouter>
    <App />
  </BrowserRouter>
);
