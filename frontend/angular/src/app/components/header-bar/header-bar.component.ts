import { Component } from '@angular/core';
import {MenuItem} from "primeng/api";
import {AuthenticationResponse} from "../../models/authentication-response";
import {JwtHelperService} from "@auth0/angular-jwt";
import {CustomerDTO} from "../../models/customer-dto";
import {Router} from "@angular/router";

@Component({
  selector: 'app-header-bar',
  templateUrl: './header-bar.component.html',
  styleUrls: ['./header-bar.component.scss']
})
export class HeaderBarComponent {
  constructor(private router:Router) {
  }
  items: MenuItem[]=[
    {label:'Profile',icon:'pi pi-user'},
    {label:'Settings',icon:'pi pi-cog'},
    {separator: true},
    {label:'Sign out',icon:'pi pi-sign-out',
    command:()=>{
      localStorage.clear();
      this.router.navigate(["login"]);
    }}
  ];

  get username(): string {
    const storedUser=localStorage.getItem("user");
    if (storedUser) {
      const authResp:AuthenticationResponse=JSON.parse(storedUser);
      if (authResp && authResp.customerDTO && authResp.customerDTO.username) {
        return authResp.customerDTO.username
      }
    }
    return '--';
  }

  get userRole(): string {
    const storedUser=localStorage.getItem("user");
    if (storedUser) {
      const authResp:AuthenticationResponse=JSON.parse(storedUser);
      if (authResp && authResp.customerDTO && authResp.customerDTO.roles) {
        return authResp.customerDTO.roles[0]
      }
    }
    return '--';
  }


}
