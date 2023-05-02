import { Injectable } from '@angular/core';
import {AuthenticationResponse} from "../../models/authentication-response";
import {JwtHelperService} from "@auth0/angular-jwt";

@Injectable({
  providedIn: 'root'
})
export class AccessGuardService {

  constructor() { }

  isLoggedIn(): boolean {
    const storedUser=localStorage.getItem("user");
    if (storedUser) {
      const authenticationResponse:AuthenticationResponse=JSON.parse(storedUser);
      if (authenticationResponse) {
        const token=authenticationResponse.token;
        if (token) {
          const jwtHelper=new JwtHelperService();
          const isTokenExpired=jwtHelper.isTokenExpired(token);
          if (!isTokenExpired) {
            return true;
          }
        }
      }
    }

    return false;
  }
}
