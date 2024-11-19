// src/services/AuthService.js
import { Amplify, Auth } from 'aws-amplify';

// Initialize Amplify with your Cognito configuration
Amplify.configure({
  Auth: {
    region: "us-east-1",
    userPoolId: "us-east-1_wc88avkny",
    clientId: "lfqu8edjkppsrl7chspdui4le",
    authenticationFlowType: 'USER_SRP_AUTH'
  }
});

class AuthService {
  async signIn(username, password) {
    try {
      const user = await Auth.signIn(username, password);
      return user;
    } catch (error) {
      throw this.handleAuthError(error);
    }
  }

  async signUp(username, password, email) {
    try {
      const { user } = await Auth.signUp({
        username,
        password,
        attributes: {
          email
        }
      });
      return user;
    } catch (error) {
      throw this.handleAuthError(error);
    }
  }

  async confirmSignUp(username, code) {
    try {
      await Auth.confirmSignUp(username, code);
    } catch (error) {
      throw this.handleAuthError(error);
    }
  }

  async signOut() {
    try {
      await Auth.signOut();
    } catch (error) {
      throw this.handleAuthError(error);
    }
  }

  async getCurrentUser() {
    try {
      const user = await Auth.currentAuthenticatedUser();
      return user;
    } catch (error) {
      console.log(error)  
      return null;
    }
  }

  async getToken() {
    try {
      const session = await Auth.currentSession();
      return session.getIdToken().getJwtToken();
    } catch (error) {
        console.log(error)    
      return null;
    }
  }

  handleAuthError(error) {
    switch (error.code) {
      case 'UserNotFoundException':
        return new Error('User not found');
      case 'NotAuthorizedException':
        return new Error('Incorrect username or password');
      case 'UserNotConfirmedException':
        return new Error('Please confirm your account');
      case 'UsernameExistsException':
        return new Error('Username already exists');
      default:
        return error;
    }
  }
}

export default new AuthService();