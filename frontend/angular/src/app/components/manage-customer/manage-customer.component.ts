import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {CustomerRegistrationRequest} from "../../models/customer-registration-request";
import {ObjectUtils} from "primeng/utils";

@Component({
  selector: 'app-manage-customer',
  templateUrl: './manage-customer.component.html',
  styleUrls: ['./manage-customer.component.scss']
})
export class ManageCustomerComponent  {

  @Input()
  customer: CustomerRegistrationRequest={};

  @Input()
  operation: 'create' | 'update'='create';

  @Output()
  submit:EventEmitter<CustomerRegistrationRequest>=new EventEmitter<CustomerRegistrationRequest>();

  @Output()
  cancel:EventEmitter<void>=new EventEmitter<void>();


  get isCustomerValid():boolean {

      const isNameValid=ObjectUtils.isNotEmpty(this.customer.name);
      const isEmailValid=ObjectUtils.isNotEmpty(this.customer.email);
      const isPasswordValid=ObjectUtils.isNotEmpty(this.customer.password)
      const isAgeValid=(this.customer.age!==undefined && this.customer.age>0);
      const isGenderValid=(this.customer.gender==='MALE' || this.customer.gender==='FEMALE');
      const isValid=isNameValid && isEmailValid  && isAgeValid &&
            (this.operation==='update' || isGenderValid && isPasswordValid);

      return isValid;
  }

  get isCreateOperation():boolean {
    return this.operation==='create';
  }

  onSubmit():void {
     this.submit.emit(this.customer);
  }


  onCancel() {
    this.cancel.emit();
  }
}
