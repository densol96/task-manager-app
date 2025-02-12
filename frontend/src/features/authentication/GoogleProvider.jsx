function GoogleProvider() {
  const googleAuthUrl =
    `https://accounts.google.com/o/oauth2/v2/auth?` +
    `scope=email%20profile&` +
    `access_type=offline&` +
    `response_type=code&` +
    `redirect_uri=http://localhost:8080/login/oauth2/code/google&` +
    `client_id=188573725163-gnurb7ro09b9q4dsl9o6so0umkb231jc.apps.googleusercontent.com`;

  const handleLogin = () => {
    window.location.href = googleAuthUrl;
  };
  return <button>GO THROUGH GOOGLE</button>;
}

export default GoogleProvider;
