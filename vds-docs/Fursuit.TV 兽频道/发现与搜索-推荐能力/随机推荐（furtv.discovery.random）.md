
# 随机推荐

获取个性化/随机推荐档案。

权限节点：`furtv.discovery.random`

## 请求头

- `X-Api-Key: <apiKey>`

## 请求地址

- `GET /api/proxy/furtv/fursuit/random`

## 查询参数

- `count`：可选，返回数量，范围 `1-20`，默认 `1`

## 成功响应示例（count=1）

```json
{
    "success": true,
    "fursuit": {
        "id": 11781,
        "username": "MCbcx",
        "nickname": "圣诞.",
        "avatar_url": "https://imageproxy-vdp.vdsentnet.com/__source/ugc.fursuit.tv/ugc/2026/0506/e7dfej7cj58agg77a7kcb9c8fk7h58j6~1784449645~tQ41gh8DHQYeyTutDbpi7hgsB10KjnMJ.jpg",
        "fursuit_species": "鲨鱼",
        "fursuit_birthday": "2026-03-14T16:00:00.000Z",
        "fursuit_maker": "",
        "showcase_portrait": "https://imageproxy-vdp.vdsentnet.com/__source/ugc.fursuit.tv/87aaciijc6g76kjhcfcdk8ck7ggb96bk~1784449645~N4K7oG7B9fqrRKm5eFvtQliybeq7mfT8.jpg",
        "showcase_landscape": "https://imageproxy-vdp.vdsentnet.com/__source/ugc.fursuit.tv/87aaciijc6g76kjhcfcdk8ck7ggb96bk~1784449645~N4K7oG7B9fqrRKm5eFvtQliybeq7mfT8.jpg",
        "showcase_other": "https://imageproxy-vdp.vdsentnet.com/__source/ugc.fursuit.tv/87aaciijc6g76kjhcfcdk8ck7ggb96bk~1784449645~N4K7oG7B9fqrRKm5eFvtQliybeq7mfT8.jpg",
        "introduction": "崽子叫“小汐”全名“凛汐”\n   平日里喜欢玩各类游戏，其中最爱的是射击类。虽说技术不是很好，但热情满分！非常欢迎大家加我一起打游戏，输赢不重要，开心最重要，说不定哪天还能给你带去意外的小惊喜。\n刚认识可能会有点内向、话不多，但熟悉之后绝对是超热情的小伙伴～ ՞˶･֊･˶՞ \n   平时相处几乎没有雷点，不过在还没完全熟络之前，还请大家多多包容，暂时不要开太多玩笑哦。\n   作为新手，还有很多需要学习的地方，但诚意满格！本人是湖南人，但因学业在广州，不过非常欢迎大家找我扩列，期待在这个圈子里结识更多有趣的朋友，一起度过快乐的时光～ (ᐢᴖ ·̫ ᴖᐢ)",
        "destinations": [],
        "destination": null,
        "destination_expires_at": null,
        "view_count": 32,
        "is_verified": false,
        "privacy_settings": {
            "profile_public": true,
            "allow_messages": true,
            "allow_return_images": true,
            "show_visitor_details": true,
            "showEmail": true,
            "showLocation": true,
            "allowContact": true,
            "show_email": true,
            "allow_contact": true,
            "show_location": true,
            "allowReturnImages": true,
            "allowMapShareInvites": true,
            "allow_map_share_invites": true
        },
        "has_all_images": true,
        "contact_info": {
            "email": "1564625591@qq.com",
            "custom": [],
            "wechat": "wxid_pqmdktmwjjpq22"
        },
        "contact_request": {
            "button_state": "disabled",
            "can_request": false,
            "reason_code": "login_required",
            "message": "登录后才能扩列",
            "requires_auth": true,
            "button_text": "登录后扩列",
            "buttonText": "登录后扩列",
            "has_completed_contact": false,
            "hasCompletedContact": false
        },
        "has_completed_contact": false,
        "hasCompletedContact": false,
        "today_status": {
            "has_today": false
        }
    },
    "debug_info": {
        "session_key": "guest_::ffff:127.0.0...",
        "is_personalized": false,
        "recommendation_mode": "guest_random_forced",
        "personalization_disabled_by_env": true,
        "curated_injections_enabled": false,
        "response_cache_enabled": false,
        "excluded_count": 63,
        "history_length": 64,
        "cache_hit_count": 1,
        "cache_queue_remaining": 19,
        "queue_cache_degraded": false,
        "request_rps": 1,
        "request_rpm": 1,
        "random_ratio": 1,
        "complexity_mode": "normal",
        "max_personalized_budget": 1,
        "latency_ema_ms": 139,
        "response_ms": 111,
        "exhaustion_recovered": false,
        "exhaustion_recovery_stage": null,
        "generated_random_count": 0,
        "generated_personalized_count": 0,
        "verified_user_target": 0,
        "verified_user_returned": 0,
        "hot_user_injected": false,
        "hot_user_injected_id": null,
        "blackbox_user_injected": false,
        "blackbox_user_injected_id": null,
        "final_top_up_count": 0,
        "final_top_up_filtered_count": 0,
        "strict_count_fill_enabled": true,
        "strict_count_fill_added": 0,
        "strict_count_fill_source": null,
        "strict_count_fill_hot_pool_added": 0,
        "strict_count_fill_hot_pool_relaxed_added": 0,
        "strict_count_fill_db_added": 0,
        "strict_count_fill_db_skipped_reason": null,
        "has_authenticated_viewer": false
    },
    "count": 1,
    "requestId": "93ed4318-b9fe-4316-a3f0-fc489ac5edc7"
}
```

## 成功响应示例（count>1）

```json
{
    "success": true,
    "fursuits": [
        {
            "id": 1,
            "username": "GeorgeBai",
            "nickname": "墨煜不是猫",
            "avatar_url": "https://imageproxy-vdp.vdsentnet.com/__source/ugc.fursuit.tv/ugc/2026/0414/77ddch66edakdc9d795chfbb58kfd9hg~1784449696~ZzuBLsFyW8Uy-4kan8gSMyVaJglCaIQc.jpg",
            "fursuit_species": "狼",
            "fursuit_birthday": "2009-11-09T16:00:00.000Z",
            "fursuit_maker": "咕咕猫w @1133527",
            "showcase_portrait": "https://imageproxy-vdp.vdsentnet.com/__source/ugc.fursuit.tv/ugc/2026/0414/g8cafab89ifcb8if96ah5e6bh8djj7k9~1784449696~dXIrQLD1rtSszlWuO-JAl0U16KJsDFQ_.jpg",
            "showcase_landscape": "https://imageproxy-vdp.vdsentnet.com/__source/ugc.fursuit.tv/ugc/2026/0414/g8cafab89ifcb8if96ah5e6bh8djj7k9~1784449696~dXIrQLD1rtSszlWuO-JAl0U16KJsDFQ_.jpg",
            "showcase_other": "https://imageproxy-vdp.vdsentnet.com/__source/ugc.fursuit.tv/ugc/2026/0414/g8cafab89ifcb8if96ah5e6bh8djj7k9~1784449696~dXIrQLD1rtSszlWuO-JAl0U16KJsDFQ_.jpg",
            "introduction": "我是白V，也可以叫墨煜。16岁，辍学，在做自己的公司。人在南京，但脑子一直在更远的地方飘。我不太喜欢把自己定义成什么“创业者”或者“创始人”，这些词有点空。如果一定要说，我更像是一个一直在把脑子里那些想法，一点点变成现实的人。\n\n我在做什么：\n我在让一些本来不被认真对待的东西，被认真对待。\n\n兽频道（Fursuit.TV）\n一个属于 Furry 圈的“数字名片 + 社交生态”。不是单纯聊天发东西的平台，而是让每一个人、每一场聚会、每一个存在，都有被看见的方式。\n\nVDS 这一整套东西\n在做的是一整条链路。从内容怎么被生产，到怎么被传播，再到怎么被人真正参与进去。说白了，就是想让“有价值的内容”，不是一闪而过，而是能留下来、持续发生。\n还有一些别的东西，反正也一直在做，也停不下来。\n\n团队这块，其实我不太爱说“团队”这个词。更像是一群真的一起在拼的人，谢谢你们。\n<at-link=@raincat> <at-link=@yeling> <at-link=@Longteng> <at-link=@YEZIMAO> <at-link=@2937721182>\n很多东西不是我一个人能撑起来的",
            "destinations": [],
            "destination": null,
            "destination_expires_at": null,
            "view_count": 9086,
            "is_verified": true,
            "privacy_settings": {
                "profile_public": true,
                "allow_messages": true,
                "allow_return_images": true,
                "show_visitor_details": true,
                "showEmail": false,
                "showLocation": true,
                "allowContact": true,
                "show_email": false,
                "allow_contact": true,
                "show_location": true,
                "allowReturnImages": true,
                "allowMapShareInvites": true,
                "contactRequestPolicy": "level",
                "contactRequestMinLevel": 2,
                "contact_request_policy": "level",
                "allow_map_share_invites": true,
                "contact_request_min_level": 2,
                "contactRequestBlockFlaggedUsers": true,
                "contact_request_block_flagged_users": true
            },
            "has_all_images": true,
            "contact_info": {
                "custom": [],
                "wechat": "WhiteBabyEatGood"
            },
            "contact_request": {
                "button_state": "disabled",
                "can_request": false,
                "reason_code": "login_required",
                "message": "登录后才能扩列",
                "requires_auth": true,
                "button_text": "登录后扩列",
                "buttonText": "登录后扩列",
                "has_completed_contact": false,
                "hasCompletedContact": false
            },
            "has_completed_contact": false,
            "hasCompletedContact": false,
            "today_status": {
                "has_today": false
            }
        },
        {
            "id": 52443,
            "username": "XYMY",
            "nickname": "XYMY",
            "avatar_url": "https://imageproxy-vdp.vdsentnet.com/__source/ugc.fursuit.tv/ugc/2026/0717/gcai5g7e5ei9i5fdeh5k9khahjacec9b~1784449696~gIExfB84dF8Pvsai3iKC9Is6WN8d1Pkw.jpg",
            "fursuit_species": "",
            "fursuit_birthday": null,
            "fursuit_maker": "",
            "showcase_portrait": "https://imageproxy-vdp.vdsentnet.com/__source/ugc.fursuit.tv/ugc/2026/0717/jfi9ka79bihf967ib9i9di7fagf8jgbg~1784449696~HeF2Akj_sNk0RIKtidNKaSTYZ74pMZxp.jpg",
            "showcase_landscape": "https://imageproxy-vdp.vdsentnet.com/__source/ugc.fursuit.tv/ugc/2026/0717/jfi9ka79bihf967ib9i9di7fagf8jgbg~1784449696~HeF2Akj_sNk0RIKtidNKaSTYZ74pMZxp.jpg",
            "showcase_other": "https://imageproxy-vdp.vdsentnet.com/__source/ugc.fursuit.tv/ugc/2026/0717/jfi9ka79bihf967ib9i9di7fagf8jgbg~1784449696~HeF2Akj_sNk0RIKtidNKaSTYZ74pMZxp.jpg",
            "introduction": "这只兽兽很神秘，还没有留下任何介绍...",
            "destinations": [],
            "destination": null,
            "destination_expires_at": null,
            "view_count": 7,
            "is_verified": false,
            "privacy_settings": {
                "profile_public": true,
                "allow_messages": true,
                "allow_return_images": true,
                "show_visitor_details": true,
                "showEmail": true,
                "showLocation": true,
                "allowContact": true,
                "show_email": true,
                "allow_contact": true,
                "show_location": true,
                "allowReturnImages": true,
                "allowMapShareInvites": true,
                "contactRequestPolicy": "level",
                "contactRequestMinLevel": 2,
                "contact_request_policy": "level",
                "allow_map_share_invites": true,
                "contact_request_min_level": 2,
                "contactRequestBlockFlaggedUsers": false,
                "contact_request_block_flagged_users": false
            },
            "has_all_images": true,
            "contact_info": {
                "custom": [],
                "wechat": "wqh123456"
            },
            "contact_request": {
                "button_state": "disabled",
                "can_request": false,
                "reason_code": "login_required",
                "message": "登录后才能扩列",
                "requires_auth": true,
                "button_text": "登录后扩列",
                "buttonText": "登录后扩列",
                "has_completed_contact": false,
                "hasCompletedContact": false
            },
            "has_completed_contact": false,
            "hasCompletedContact": false,
            "today_status": {
                "has_today": false
            }
        },
        {
            "id": 675,
            "username": "xiangwan",
            "nickname": "向晚",
            "avatar_url": "https://imageproxy-vdp.vdsentnet.com/__source/ugc.fursuit.tv/ecfe6ikd6je5kdfeeeai8hedchjabidb~1784449696~1pLqT-ivoIPdL5YHwtKmQCHoEdJ6euSo.jpg",
            "fursuit_species": "狼狗",
            "fursuit_birthday": "2025-12-19T16:00:00.000Z",
            "fursuit_maker": "西哒豆丁",
            "showcase_portrait": "https://imageproxy-vdp.vdsentnet.com/__source/ugc.fursuit.tv/gdbekgahcka8db5g9egb6dbb66gjddg6~1784449696~Y-tH_q_1T93NFCLGG40qXY-dDDLpqF86.jpg",
            "showcase_landscape": "https://imageproxy-vdp.vdsentnet.com/__source/ugc.fursuit.tv/gdbekgahcka8db5g9egb6dbb66gjddg6~1784449696~Y-tH_q_1T93NFCLGG40qXY-dDDLpqF86.jpg",
            "showcase_other": "https://imageproxy-vdp.vdsentnet.com/__source/ugc.fursuit.tv/gdbekgahcka8db5g9egb6dbb66gjddg6~1784449696~Y-tH_q_1T93NFCLGG40qXY-dDDLpqF86.jpg",
            "introduction": "进来就别走啦(｡･ω･｡)～行程：hifurry，这里向晚，浓度超高，喜欢出毛咔咔拍照，是一只活泼开朗的元气小狼狗，已有版权©请勿盗用。",
            "destinations": [],
            "destination": null,
            "destination_expires_at": null,
            "view_count": 561,
            "is_verified": false,
            "privacy_settings": {
                "profile_public": true,
                "allow_messages": true,
                "allow_return_images": true,
                "show_visitor_details": true,
                "showEmail": true,
                "showLocation": true,
                "allowContact": true
            },
            "has_all_images": true,
            "contact_info": {
                "email": "1658146445@qq.com",
                "custom": []
            },
            "contact_request": {
                "button_state": "disabled",
                "can_request": false,
                "reason_code": "login_required",
                "message": "登录后才能扩列",
                "requires_auth": true,
                "button_text": "登录后扩列",
                "buttonText": "登录后扩列",
                "has_completed_contact": false,
                "hasCompletedContact": false
            },
            "has_completed_contact": false,
            "hasCompletedContact": false,
            "today_status": {
                "has_today": false
            }
        }
    ],
    "count": 3,
    "requested_count": 3,
    "debug_info": {
        "session_key": "guest_::ffff:127.0.0...",
        "is_personalized": false,
        "recommendation_mode": "guest_random_forced",
        "personalization_disabled_by_env": true,
        "curated_injections_enabled": false,
        "response_cache_enabled": false,
        "excluded_count": 64,
        "history_length": 67,
        "cache_hit_count": 3,
        "cache_queue_remaining": 16,
        "queue_cache_degraded": false,
        "request_rps": 1,
        "request_rpm": 2,
        "random_ratio": 1,
        "complexity_mode": "normal",
        "max_personalized_budget": 1,
        "latency_ema_ms": 131,
        "response_ms": 107,
        "exhaustion_recovered": false,
        "exhaustion_recovery_stage": null,
        "generated_random_count": 0,
        "generated_personalized_count": 0,
        "verified_user_target": 0,
        "verified_user_returned": 1,
        "hot_user_injected": false,
        "hot_user_injected_id": null,
        "blackbox_user_injected": false,
        "blackbox_user_injected_id": null,
        "final_top_up_count": 0,
        "final_top_up_filtered_count": 0,
        "strict_count_fill_enabled": true,
        "strict_count_fill_added": 0,
        "strict_count_fill_source": null,
        "strict_count_fill_hot_pool_added": 0,
        "strict_count_fill_hot_pool_relaxed_added": 0,
        "strict_count_fill_db_added": 0,
        "strict_count_fill_db_skipped_reason": null,
        "has_authenticated_viewer": false
    },
    "requestId": "f77b1ebb-a67a-4dd3-88f8-aee83075cf7f"
}
```
