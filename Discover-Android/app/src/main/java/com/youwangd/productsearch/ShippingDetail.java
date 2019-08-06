package com.youwangd.productsearch;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.wssholmes.stark.circular_score.CircularScoreView;

import org.json.JSONObject;


public class ShippingDetail extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_shipping_detail, container, false);
        JSONObject detailData = ((DetailContainer) getActivity()).getDetailResult();
        String shipping_cost = ((DetailContainer) getActivity()).getShipping_cost();

        ScrollView shipping_scroll_view = v.findViewById(R.id.ship_scroll_view);
        TextView no_result = v.findViewById(R.id.shipping_no_result);

        LinearLayout store_section = v.findViewById(R.id.store_section);
        LinearLayout store_name_layout = v.findViewById(R.id.store_name_layout);
        LinearLayout store_feedback_layout = v.findViewById(R.id.store_feedback_layout);
        LinearLayout store_popularity_layout = v.findViewById(R.id.store_popularity_layout);
        LinearLayout store_star_layout = v.findViewById(R.id.store_star_layout);
        TextView store_name = v.findViewById(R.id.shipping_store_name);
        TextView feedback_score = v.findViewById(R.id.shipping_feedback_score);
        CircularScoreView store_popularity = v.findViewById(R.id.shipping_popularity);
        ImageView feedback_star = v.findViewById(R.id.shipping_star);

        LinearLayout shipping_section = v.findViewById(R.id.shipping_section);
        LinearLayout shipping_cost_layout = v.findViewById(R.id.shipping_cost_layout);
        LinearLayout shipping_global_layout = v.findViewById(R.id.shipping_global_layout);
        LinearLayout shipping_time_layout = v.findViewById(R.id.shipping_time_layout);
        LinearLayout shipping_condition_layout = v.findViewById(R.id.shipping_condition_layout);
        TextView shipping_cost_text = v.findViewById(R.id.shipping_cost);
        TextView global_shipping = v.findViewById(R.id.shipping_global);
        TextView shipping_time = v.findViewById(R.id.shipping_time);
        TextView shipping_condition = v.findViewById(R.id.shipping_condition);



        LinearLayout return_section = v.findViewById(R.id.return_section);
        LinearLayout return_policy_layout = v.findViewById(R.id.return_policy_layout);
        LinearLayout return_time_layout = v.findViewById(R.id.return_time_layout);
        LinearLayout return_refund_layout = v.findViewById(R.id.return_refund_layout);
        LinearLayout shipped_by_layout = v.findViewById(R.id.shipped_by_layout);
        TextView return_policy = v.findViewById(R.id.policy);
        TextView return_time = v.findViewById(R.id.return_time);
        TextView refund_mode = v.findViewById(R.id.refund);
        TextView shipped_by = v.findViewById(R.id.shipped_by);

        if(detailData.optString("Ack").equals("Success")) {
            JSONObject item = detailData.optJSONObject("Item");

            if(!item.has("Storefront") && !item.has("Seller")) {
                store_section.setVisibility(View.GONE);
            }

            if(item.has("Storefront")) {
                String storeURL = item.optJSONObject("Storefront").optString("StoreURL");
                String storeName = item.optJSONObject("Storefront").optString("StoreName");
                String html = "<font color='#d395ff'><a href='" + storeURL + "'>" + storeName + "</a>";
                store_name.setText(Html.fromHtml(html));
                store_name.setMovementMethod(LinkMovementMethod.getInstance());
            } else {
                store_name_layout.setVisibility(View.GONE);
            }
            if(item.has("Seller")) {
                JSONObject seller = item.optJSONObject("Seller");

                if(seller.has("FeedbackScore")) {
                    String score = seller.optString("FeedbackScore");
                    feedback_score.setText(score);
                } else {
                    store_feedback_layout.setVisibility(View.GONE);
                }

                if(seller.has("PositiveFeedbackPercent")) {
                    double popularity = seller.optDouble("PositiveFeedbackPercent");
                    store_popularity.setScore((int)Math.round(popularity));
                } else {
                    store_popularity_layout.setVisibility(View.GONE);
                }
                if(seller.has("FeedbackRatingStar")) {
                    String star_name = seller.optString("FeedbackRatingStar");
                    feedback_star.setImageResource(selectStarImg(star_name));
                } else {
                    store_star_layout.setVisibility(View.GONE);
                }
            } else {
                store_feedback_layout.setVisibility(View.GONE);
                store_popularity_layout.setVisibility(View.GONE);
                store_star_layout.setVisibility(View.GONE);
            }

            if(shipping_cost.equals("N/A") && !item.has("GlobalShipping") && !item.has("HandlingTime") && !item.has("ConditionDisplayName")) {
                shipping_section.setVisibility(View.GONE);
            }

            if(shipping_cost.equals("N/A")) {
                shipping_cost_layout.setVisibility(View.GONE);
            } else {
                shipping_cost_text.setText(shipping_cost);
            }

            if(item.has("GlobalShipping")) {
                if(item.optBoolean("GlobalShipping")) {
                    global_shipping.setText("Yes");
                } else {
                    global_shipping.setText("No");
                }
            } else {
                shipping_global_layout.setVisibility(View.GONE);
            }

            if(item.has("HandlingTime")) {
                if(item.optInt("HandlingTime") <=1) {
                    shipping_time.setText(Integer.toString(item.optInt("HandlingTime")) + "day");
                } else {
                    shipping_time.setText(Integer.toString(item.optInt("HandlingTime")) + "days");
                }
            } else {
                shipping_time_layout.setVisibility(View.GONE);
            }

            if(item.has("ConditionDescription")) {
                shipping_condition.setText(item.optString("ConditionDescription"));
            } else {
                shipping_condition_layout.setVisibility(View.GONE);
            }

            if(item.has("ReturnPolicy")) {
                JSONObject return_data = item.optJSONObject("ReturnPolicy");

                if(return_data.has("ReturnsAccepted")) {
                    return_policy.setText(return_data.optString("ReturnsAccepted"));
                } else {
                    return_policy_layout.setVisibility(View.GONE);
                }

                if(return_data.has("ReturnsWithin")) {
                    return_time.setText(return_data.optString("ReturnsWithin"));
                } else {
                    return_time_layout.setVisibility(View.GONE);
                }

                if(return_data.has("Refund")) {
                    refund_mode.setText(return_data.optString("Refund"));
                } else {
                    return_refund_layout.setVisibility(View.GONE);
                }

                if(return_data.has("ShippingCostPaidBy")) {
                    shipped_by.setText(return_data.optString("ShippingCostPaidBy"));
                } else {
                    shipped_by_layout.setVisibility(View.GONE);
                }
            } else {
                return_section.setVisibility(View.GONE);
            }

        } else {
            shipping_scroll_view.setVisibility(View.GONE);
            no_result.setVisibility(View.VISIBLE);
        }
        return v;
    }

    public int selectStarImg(String star) {
        if(star.equals("Yellow")) {
            return R.drawable.low_score_yellow;
        } else if(star.equals("Blue")) {
            return R.drawable.low_score_blue;
        } else if(star.equals("Turquoise")) {
            return R.drawable.low_score_turquoise;
        } else if(star.equals("Purple")) {
            return R.drawable.low_score_purple;
        } else if(star.equals("Red")) {
            return R.drawable.low_score_red;
        } else if(star.equals("Green")) {
            return R.drawable.low_score_green;
        } else if(star.equals("YellowShooting")) {
            return R.drawable.high_score_yellow;
        } else if(star.equals("TurquoiseShooting")) {
            return R.drawable.high_score_turquoise;
        } else if(star.equals("PurpleShooting")) {
            return R.drawable.high_score_purple;
        } else if(star.equals("RedShooting")) {
            return R.drawable.high_score_red;
        } else if(star.equals("GreenShooting")) {
            return R.drawable.high_score_green;
        } else if(star.equals("SilverShooting")) {
            return R.drawable.high_score_silver;
        } else {
            return R.drawable.low_score_none;
        }
    }
}
