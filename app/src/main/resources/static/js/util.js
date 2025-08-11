// util.js
  function setRole(role) {
    localStorage.setItem("userRole", role);
  }
  
  function getRole() {
    return localStorage.getItem("userRole");
  }
  
  function clearRole() {
    localStorage.removeItem("userRole");
  }

  function debounce(fn, delay = 300) {
    let t;
    return (...args) => {
      clearTimeout(t);
      t = setTimeout(() => fn.apply(null, args), delay);
    };
  }
  
  
