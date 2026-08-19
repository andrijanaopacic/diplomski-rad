import { createContext, useContext, useState } from 'react'

const AuthContext = createContext(null)

export function AuthProvider({ children }) {
  const [token, setToken] = useState(localStorage.getItem('token'))
  const [korisnik, setKorisnik] = useState(() => {
    const saved = localStorage.getItem('korisnik')
    return saved ? JSON.parse(saved) : null
  })

  function login(authResponse) {
    localStorage.setItem('token', authResponse.token)
    localStorage.setItem('korisnik', JSON.stringify(authResponse.korisnik))
    setToken(authResponse.token)
    setKorisnik(authResponse.korisnik)
  }

  function logout() {
    localStorage.removeItem('token')
    localStorage.removeItem('korisnik')
    setToken(null)
    setKorisnik(null)
  }

  const value = {
    token,
    korisnik,
    isLoggedIn: !!token,
    login,
    logout,
  }

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}

export function useAuth() {
  return useContext(AuthContext)
}