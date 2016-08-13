/*
 * Copyright 2016 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.terasology.TutorialDynamicCities.market;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.TutorialDynamicCities.dialogs.actions.ShowMarketScreenAction;
import org.terasology.dialogs.components.DialogComponent;
import org.terasology.dialogs.components.DialogPage;
import org.terasology.dialogs.components.DialogResponse;
import org.terasology.dynamicCities.buildings.GenericBuildingComponent;
import org.terasology.dynamicCities.buildings.components.DynParcelRefComponent;
import org.terasology.dynamicCities.buildings.components.SettlementRefComponent;
import org.terasology.dynamicCities.construction.events.BuildingEntitySpawnedEvent;
import org.terasology.dynamicCities.parcels.DynParcel;
import org.terasology.dynamicCities.settlements.components.MarketComponent;
import org.terasology.entitySystem.entity.EntityManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.prefab.Prefab;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.math.geom.Rect2i;
import org.terasology.math.geom.Vector3f;
import org.terasology.registry.In;
import org.terasology.utilities.Assets;

import java.util.ArrayList;
import java.util.Optional;

@RegisterSystem
public class TraderSpawnSystem extends BaseComponentSystem {

    @In
    private EntityManager entityManager;

    private Logger logger = LoggerFactory.getLogger(TraderSpawnSystem.class);

    @ReceiveEvent(components = GenericBuildingComponent.class)
    public void onMarketPlaceSpawn(BuildingEntitySpawnedEvent event, EntityRef entityRef) {
        GenericBuildingComponent genericBuildingComponent = entityRef.getComponent(GenericBuildingComponent.class);
        if (genericBuildingComponent.name.equals("marketplace")) {
            DynParcel dynParcel = entityRef.getComponent(DynParcelRefComponent.class).dynParcel;

            Optional<Prefab> blackPawnOptional = Assets.getPrefab("LightAndShadowResources:blackKing");
            if (blackPawnOptional.isPresent()) {
                Rect2i rect2i = dynParcel.shape;
                Vector3f spawnPosition = new Vector3f(rect2i.minX() + rect2i.sizeX() / 2, dynParcel.getHeight() + 1, rect2i.minY() + rect2i.sizeY() / 2);
                EntityRef trader = entityManager.create(blackPawnOptional.get(), spawnPosition);
                SettlementRefComponent settlementRefComponent =entityRef.getComponent(SettlementRefComponent.class);
                trader.addComponent(settlementRefComponent);
                MarketComponent marketComponent = settlementRefComponent.settlement.getComponent(MarketComponent.class);
                DialogComponent dialogComponent = new DialogComponent();
                DialogPage dialogPage = new DialogPage();
                dialogComponent.pages = new ArrayList<>();
                DialogResponse dialogResponse = new DialogResponse();
                dialogPage.paragraphText = new ArrayList<>();
                dialogPage.responses = new ArrayList<>();
                dialogResponse.action = new ArrayList<>();

                dialogPage.id = "MainScreen";

                dialogPage.paragraphText.add("What would you like to talk about?");
                dialogPage.title = "Welcome to the market";

                dialogResponse.text = "Show me what you got!";

                dialogResponse.action.add(new ShowMarketScreenAction(marketComponent.market.getId()));

                dialogPage.responses.add(dialogResponse);
                dialogComponent.pages.add(dialogPage);
                dialogComponent.firstPage = dialogPage.id;
                trader.addComponent(dialogComponent);
            }
        }
    }
}
