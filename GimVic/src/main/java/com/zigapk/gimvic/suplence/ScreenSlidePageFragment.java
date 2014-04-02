package com.zigapk.gimvic.suplence;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by ziga on 12/17/13.
 */
public class ScreenSlidePageFragment extends Fragment {

    /**
     * The argument key for the page number this fragment represents.
     */
    public static final String ARG_PAGE = "page";

    /**
     * The fragment's page number, which is set to the argument value for {@link #ARG_PAGE}.
     */
    private int mPageNumber;

    /**
     * Factory method for this fragment class. Constructs a new fragment for the given page number.
     */
    public static ScreenSlidePageFragment create(int pageNumber) {
        ScreenSlidePageFragment fragment = new ScreenSlidePageFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, pageNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public ScreenSlidePageFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPageNumber = getArguments().getInt(ARG_PAGE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout containing a title and body text.

        int about = R.layout.fragment_screen_slide_page_1;

        if(mPageNumber==0){
            about = R.layout.fragment_screen_slide_page_1;
        }else if(mPageNumber==1){
            about = R.layout.fragment_screen_slide_page_2;
        }else if(mPageNumber==2){
            about = R.layout.fragment_screen_slide_page_3;
        }else if(mPageNumber==3){
            about = R.layout.fragment_screen_slide_page_4;
        }else if(mPageNumber==4){
            about = R.layout.fragment_screen_slide_page_5;
        }

        ViewGroup rootView = (ViewGroup) inflater.inflate(about, container, false);

        // Set the title view to show the page number.

		/*if(mPageNumber==0)
        ((TextView) rootView.findViewById(android.R.id.text1)).setText("GimVic - suplence");*/

        return rootView;
    }

    /**
     * Returns the page number represented by this fragment object.
     */
    public int getPageNumber() {
        return mPageNumber;
    }


}
