import {Component, EventEmitter, Output} from '@angular/core';
import {CustomerRegistrationRequest} from "../../models/customer-registration-request";
import {ObjectUtils} from "primeng/utils";
import {CustomerService} from "../../services/customer/customer.service";
import {Router} from "@angular/router";
import {AuthenticationService} from "../../services/authentication/authentication.service";
import {AuthenticationRequest} from "../../models/authentication-request";

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss']
})
export class RegisterComponent {
  constructor(private customerService:CustomerService,
              private authenticationService: AuthenticationService,
              private router: Router) {
  }
  errorMsg='';

  customer: CustomerRegistrationRequest={ };


  get isCustomerValid():boolean {
    const isEmailValid=ObjectUtils.isNotEmpty(this.customer.email);
    const isNameValid=ObjectUtils.isNotEmpty(this.customer.name);

    const isPasswordValid=ObjectUtils.isNotEmpty(this.customer.password)
    const isAgeValid=(this.customer.age!==undefined && this.customer.age>0);
    const isGenderValid=(this.customer.gender==='MALE' || this.customer.gender==='FEMALE');
    const isValid=isNameValid && isEmailValid  && isAgeValid && isGenderValid && isPasswordValid;

    return isValid;
  }

  login(authenticationRequest: AuthenticationRequest) {
    this.errorMsg='';
    this.authenticationService.login(authenticationRequest)
      .subscribe({
        next: (authenticationResponse)=>{
          // console.log(authenticationResponse);

          localStorage.setItem("user",JSON.stringify(authenticationResponse));
          this.router.navigate(["customers"]);
        },
        error: (err)=> {
          console.log(err);
          console.log(err.status);
          if (err.status ===401) {
            this.errorMsg="Login and / or password is incorrect."
          }
        }
      });
  }

  signup():void {
    if (this.customer) {
        this.customerService.registerCustomer(this.customer).subscribe({
          next:()=> {
            console.log("registration succeeded=>login")
            const authReq:AuthenticationRequest={
              username:this.customer.email,
              password:this.customer.password
            };
            this.login(authReq);
            this.customer={};
          }
        });
    }
  }
}
