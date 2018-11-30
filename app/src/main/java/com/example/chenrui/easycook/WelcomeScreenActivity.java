package com.example.chenrui.easycook;

import com.stephentuso.welcome.BasicPage;
import com.stephentuso.welcome.TitlePage;
import com.stephentuso.welcome.WelcomeActivity;
import com.stephentuso.welcome.WelcomeConfiguration;

public class WelcomeScreenActivity extends WelcomeActivity {
    @Override
    protected WelcomeConfiguration configuration() {
        return new WelcomeConfiguration.Builder(this)
                .defaultTitleTypefacePath("Montserrat-Bold.ttf")
                .defaultHeaderTypefacePath("Montserrat-Bold.ttf")
                .defaultBackgroundColor(R.color.colorAccent)

                .page(new TitlePage(R.drawable.logotransparent,
                        "Welcome to EasyCook")
                        .background(R.color.welcome_screen_1)
                )
                .page(new BasicPage(R.drawable.pizza,
                        "Discovery",
                        "Discovery your favorite food and follow the touch-free instruction step by step")
                        .background(R.color.welcome_screen_2)
                )
                .page(new BasicPage(R.drawable.milk,
                        "Shopping List",
                        "Add missing ingredients to the list and save for future buying.")
                        .background(R.color.welcome_screen_3)
                )
                .page(new BasicPage(R.drawable.gloves,
                        "My RecipesFragment",
                        "Customized your own recipes and share with public")
                        .background(R.color.welcome_screen_4)
                )

                .swipeToDismiss(true)
                .exitAnimation(android.R.anim.fade_out)
                .build();
    }

    public static String welcomeKey() {
        return "WelcomeScreen";
    }
}
