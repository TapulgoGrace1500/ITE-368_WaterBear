package com.example.waterbear2021;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class OrderToBeDeliverFragment extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference orderRef = db.collection("Orders");
    private CollectionReference userRef = db.collection("Customers");
    private String buyerID, id, status;
    ;

    private RecyclerView orderTobeDeliverRecyclerView;
    private ArrayList<OrderDetail> options;
    private OrderToBeDeliverAdapter orderToBeDeliverAdapter;

    public OrderToBeDeliverFragment(){
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_order_tobedeliver,container,false);

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        orderTobeDeliverRecyclerView = view.findViewById(R.id.orders_tobedeliver);

        options = new ArrayList<>();
        orderTobeDeliverRecyclerView.setHasFixedSize(true);
        orderTobeDeliverRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        SetUpOrderPendingRecyclerView();
    }
    private void SetUpOrderPendingRecyclerView() {
        try {

            orderRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot documentSnapshots) {
                    for (DocumentSnapshot snapshot: documentSnapshots){
                        try {
                            buyerID = snapshot.getString("Customer ID");


                            try {
                                orderRef.document(buyerID)
                                        .collection("Store").document(mAuth.getCurrentUser().getUid())
                                        .collection("Customer").document(buyerID)
                                        .collection("Cart").whereEqualTo("Order Status", "ToBeDeliver").addSnapshotListener(new EventListener<QuerySnapshot>() {
                                    @Override
                                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                        if (value != null) {
                                            for (DocumentSnapshot snapshot : value) {

                                                OrderDetail orderDetail = new OrderDetail();

                                                try {

                                                    orderDetail.setOrderId(snapshot.getString("Order ID"));
                                                    orderDetail.setCartId(snapshot.getString("Cart ID"));
                                                    orderDetail.setOrderStoreId(snapshot.getString("Store ID"));
                                                    orderDetail.setOrderStoreName(snapshot.getString("Store Name"));
                                                    orderDetail.setOrderBuyerId(snapshot.getString("Customer ID"));
                                                    orderDetail.setOrderBuyerName(snapshot.getString("Customer Name"));
                                                    orderDetail.setOrderBuyerAddress(snapshot.getString("Customer Address"));
                                                    orderDetail.setOrderBuyerContactNumber(snapshot.getString("Contact Number"));
                                                    orderDetail.setOrderDeliveryFee(snapshot.getDouble("Delivery Fee"));
                                                    orderDetail.setOrderTotal(snapshot.getDouble("Order Total"));
                                                    orderDetail.setOrderStatus(snapshot.getString("Order Status"));

                                                    options.add(orderDetail);
                                                } catch (Exception e) {
                                                    Log.e("TAG", "Error to display cart item! " + e.getMessage());
                                                    e.printStackTrace();
                                                }

                                            }
                                        }

                                        orderToBeDeliverAdapter = new OrderToBeDeliverAdapter(getContext(), options);
                                        orderTobeDeliverRecyclerView.setAdapter(orderToBeDeliverAdapter);
                                        orderToBeDeliverAdapter.notifyDataSetChanged();
                                    }

                                });
                            }
                            catch (Exception e){
                                Log.e("TAG","Error in getting storeID"+"\n"+e.getMessage());
                                e.printStackTrace();
                            }

                        }
                        catch (Exception e){
                            Log.e("TAG","Failed to get store id");
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
        catch (Exception e){
            Log.e("TAG","Failed to execute path! "+e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {

        if (item.getTitle().equals("Delete Order")){
            deleteOrder(orderToBeDeliverAdapter.getItem(item.getOrder()).getOrderId()
                    ,orderToBeDeliverAdapter.getItem(item.getOrder()).getOrderBuyerId()
                    ,orderToBeDeliverAdapter.getItem(item.getOrder()).getOrderStatus()
                    ,orderToBeDeliverAdapter.getItem(item.getOrder()));
        }

        return super.onContextItemSelected(item);
    }

    private void deleteOrder(String orderId, String orderBuyerId, String orderStatus, OrderDetail item) {

        userRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    userRef.document(orderBuyerId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {

                            id = documentSnapshot.getId();
                            status = documentSnapshot.getString("isBuyer");

                            if (id.equals(orderBuyerId)){
                                assert status != null;
                                confirm(status);
                            }
                        }
                    });
                }
            }

            private void confirm(String status) {
                if (status.equals("Spam")){

                    orderRef.document(orderBuyerId).collection("Store")
                            .document(mAuth.getCurrentUser().getUid()).collection("Customer")
                            .document(orderBuyerId).collection("Cart")
                            .document(orderId).delete();

                    getActivity().finish();
                    Toast.makeText(getContext(), "Successfully Deleted an Order!", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getContext(), "Unable to Delete Order!", Toast.LENGTH_SHORT).show();
                }
            }

        });
    }
}
