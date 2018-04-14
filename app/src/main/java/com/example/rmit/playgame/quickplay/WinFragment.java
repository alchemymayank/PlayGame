package com.example.rmit.playgame.quickplay;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.rmit.playgame.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link WinFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link WinFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WinFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "TrueQues";
    private static final String ARG_PARAM2 = "FalseQues";
    private static final String ARG_PARAM3 = "DropQues";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String mParam3;

    private OnFragmentInteractionListener mListener;

    TextView textViewTrue, textViewFalse, textViewStatus, textViewDrop;
    ImageView imageViewThumbs;

    public WinFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment WinFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static WinFragment newInstance(String param1, String param2, String param3) {
        WinFragment fragment = new WinFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        args.putString(ARG_PARAM3, param3);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            mParam3 = getArguments().getString(ARG_PARAM3);

            Log.d("MyTag","OnCreate Called");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d("MyTag","OnCreateView Called");

        View view = inflater.inflate(R.layout.fragment_win, container, false);
        textViewTrue = (TextView)view.findViewById(R.id.true_ques);
        textViewFalse = (TextView) view.findViewById(R.id.false_ques);
        textViewDrop = (TextView) view.findViewById(R.id.drop_ques);
        textViewStatus = (TextView)view.findViewById(R.id.game_status);
        imageViewThumbs = (ImageView) view.findViewById(R.id.image_thumb);textViewTrue.setText("True Questions : "+ mParam1);
        textViewFalse.setText("False Questions : "+ mParam2);
        textViewDrop.setText("Drop Questions : "+ mParam3);
        int trueQ = Integer.parseInt(mParam1);
        int falseQ = Integer.parseInt(mParam2);
        if (trueQ>falseQ){
            imageViewThumbs.setImageResource(R.drawable.ic_thumb_up_green_900_24dp);
            textViewStatus.setText("Congrats! You Win!");
        }else {
            imageViewThumbs.setImageResource(R.drawable.ic_thumb_down_red_900_24dp);
            textViewStatus.setText("Sorry! You Loose!");
        }

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        Log.d("MyTag","OnAttach Called");
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        Log.d("MyTag","OnDetach Called");
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
