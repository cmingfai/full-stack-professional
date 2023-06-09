import axios from "axios";

const getAuthConfig=() => ({
  headers: {
      Authorization: `Bearer ${localStorage.getItem("access_token")}`
  }
})

const getCustomers=async () => {
  try {
      return await axios.get(`${import.meta.env.VITE_API_BASE_URL}/api/v1/customers`,
          getAuthConfig())
  }   catch(e) {
      throw e
  }
}

const saveCustomer=async (customer) => {
    try {
        return await axios.post(`${import.meta.env.VITE_API_BASE_URL}/api/v1/customers`,
            customer)
    } catch(e) {
        throw e
    }
}
const deleteCustomer=async (id) => {
    try {
        return await axios.delete(`${import.meta.env.VITE_API_BASE_URL}/api/v1/customers/${id}`,
            getAuthConfig())
    } catch(e) {
        throw e
    }
}

const updateCustomer=async (id, customer) => {
    try {
        return await axios.put(`${import.meta.env.VITE_API_BASE_URL}/api/v1/customers/${id}`,
            customer,
            getAuthConfig())
    } catch(e) {
        throw e
    }
}


const login=async (usernameAndPassword) => {
    try {
        return await axios.post(`${import.meta.env.VITE_API_BASE_URL}/api/v1/auth/login`
            ,usernameAndPassword)
    } catch(e) {
        throw e
    }
}

const uploadCustomerProfilePicture=async (id, formData) => {
    try {
        return await axios.post(`${import.meta.env.VITE_API_BASE_URL}/api/v1/customers/${id}/profile-image`,
            formData,
            {
                ...getAuthConfig(),
                'Content-Type':'multipart/form-data'
            })
    } catch(e) {
        throw e
    }
}

const customerProfilePictureUrl= (id) => `${import.meta.env.VITE_API_BASE_URL}/api/v1/customers/${id}/profile-image`;

export {getCustomers,
    saveCustomer,
    deleteCustomer,
    updateCustomer,
    login,
    uploadCustomerProfilePicture,
    customerProfilePictureUrl}