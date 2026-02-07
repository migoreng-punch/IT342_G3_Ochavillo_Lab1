import { useState } from "react";
import { TextField, Button, Box, Typography, Paper } from "@mui/material";
import api from "../api/axios";
import { useNavigate } from "react-router-dom";

const Register = () => {
  const [form, setForm] = useState({
    username: "",
    email: "",
    password: "",
  });

  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      await api.post("/api/auth/register", form);
      navigate("/login");
    } catch {
      alert("Registration failed");
    }
  };

  return (
    <Box
      display="flex"
      justifyContent="center"
      alignItems="center"
      height="100vh"
    >
      <Paper elevation={3} sx={{ p: 4, width: 350 }}>
        <Typography variant="h5" mb={2} textAlign="center">
          Register
        </Typography>

        <Box component="form" onSubmit={handleSubmit}>
          <TextField
            label="Username"
            fullWidth
            margin="normal"
            onChange={(e) =>
              setForm({ ...form, username: e.target.value })
            }
          />
          <TextField
            label="Email"
            fullWidth
            margin="normal"
            onChange={(e) =>
              setForm({ ...form, email: e.target.value })
            }
          />
          <TextField
            label="Password"
            type="password"
            fullWidth
            margin="normal"
            onChange={(e) =>
              setForm({ ...form, password: e.target.value })
            }
          />
          <Button
            type="submit"
            variant="contained"
            fullWidth
            sx={{ mt: 2 }}
          >
            Register
          </Button>
        </Box>
      </Paper>
    </Box>
  );
};

export default Register;
